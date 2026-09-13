package com.lovelycatv.vertex.ai.llm

import com.google.gson.Gson
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import com.lovelycatv.vertex.ai.exception.RequestException
import com.lovelycatv.vertex.ai.llm.config.LLMClientConfig
import com.lovelycatv.vertex.ai.llm.message.StopReason
import com.lovelycatv.vertex.ai.llm.message.ToolCall
import com.lovelycatv.vertex.ai.utils.tryRead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.util.concurrent.TimeUnit
import kotlin.collections.iterator

abstract class LLMClient(
    protected val llmClientConfig: LLMClientConfig
) {
    protected val gson = Gson()

    private val client = OkHttpClient.Builder()
        .connectTimeout(llmClientConfig.connectionTimeoutSeconds, TimeUnit.SECONDS)
        .readTimeout(llmClientConfig.readTimeoutSeconds, TimeUnit.SECONDS)
        .build()

    suspend fun chatCompletion(chatRequest: ChatRequest): ChatResponse {
        val jsonResponse = readBody(this.executeRequest(chatRequest))

        return this.resolveChatResponse(
            this.toChatResponseResolveContext(chatRequest, jsonResponse)
        )
    }

    /**
     * Lists every model the endpoint offers.
     *
     * Pagination is handled here so callers see the whole catalogue either way — the
     * OpenAI-compatible endpoint returns it in a single response, while the Messages API pages
     * through it.
     */
    suspend fun listModels(): List<LLMModel> {
        val models = mutableListOf<LLMModel>()
        var cursor: String? = null

        while (true) {
            val page = resolveModelsPage(
                readBody(execute(buildGetRequest(buildModelsUrl(cursor))))
            )
            models += page.models

            // A cursor that repeats itself would loop forever; treat it as the end.
            val next = page.nextCursor
            if (next == null || next == cursor) {
                return models
            }

            cursor = next
        }
    }

    private suspend fun readBody(response: Response): String {
        return withContext(Dispatchers.IO) {
            response.body?.string()
        } ?: throw RequestException("No response body")
    }

    /**
     * Builds the Models URL for one page. Providers that return the whole catalogue at once
     * ignore [cursor] and use the plain path.
     */
    open fun buildModelsUrl(cursor: String?): String {
        return getRequestUrl(llmClientConfig.modelsPath)
    }

    abstract fun resolveModelsPage(responseBody: String): LLMModelPage

    /**
     * Parses a Models response body into a map. Every provider wraps its catalogue in a `data`
     * array alongside other, provider-specific fields.
     */
    protected fun parseModelsResponse(responseBody: String): Map<String, Any?> {
        val responseMap = try {
            tryCast2StringMap(gson.fromJson(responseBody, Map::class.java))
        } catch (_: Exception) {
            null
        }

        return responseMap ?: throw RequestException("Could not parse models response: $responseBody")
    }

    /** The `data` array carried by a Models response, per [parseModelsResponse]. */
    protected fun modelsDataOf(responseMap: Map<String, Any?>): List<Map<String, Any?>> {
        return tryCast2StringMapList(responseMap["data"])
            ?: throw RequestException("Models response has no data array: $responseMap")
    }

    /** One page of a Models response, plus the cursor that reads the next one. */
    class LLMModelPage(
        val models: List<LLMModel>,
        val nextCursor: String? = null,
    )

    suspend fun chatCompletionAsync(chatRequest: ChatRequest): Flow<StreamChatResponse> {
        val response = this.executeRequest(chatRequest)

        val bufferSource = response.body?.source()
            ?: throw RequestException("No response body")

        return flow {
            bufferSource.use { source ->
                while (!source.exhausted()) {
                    val line = source.readUtf8Line()

                    if (line.isNullOrBlank()) continue

                    if (line.startsWith(SSE_DATA_FLAG)) {
                        val data = line.substring(SSE_DATA_FLAG.length).trim()

                        if (data == SSE_STOP_FLAG) {
                            break
                        }

                        val response = this@LLMClient.resolveStreamChatResponse(
                            this@LLMClient.toChatResponseResolveContext(chatRequest, data)
                        )

                        emit(response)
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    abstract fun transformRequestBody(chatRequest: ChatRequest): String

    protected abstract fun resolveChatResponse(ctx: ChatResponseResolveContext): ChatResponse

    protected abstract fun resolveStreamChatResponse(ctx: ChatResponseResolveContext): StreamChatResponse

    abstract fun routeStopReason(stopReasonString: String?): StopReason?

    /**
     * Body entries contributed by [ChatRequest.reasoningEffort] — a single `reasoning_effort` for
     * the OpenAI-compatible protocol, the `thinking` / `output_config` pair for the Messages API.
     *
     * These are merged after the fields derived from the request and before
     * [ChatRequest.extraBody], so an override there still wins. An empty map leaves the provider's
     * own default in place.
     */
    protected abstract fun resolveReasoningConfig(reasoningEffort: ReasoningEffort): Map<String, Any?>

    protected fun resolveUsage(ctx: ChatResponseResolveContext): ChatResponse.Usage {
        val promptTokens = llmClientConfig.llmResponseConfig.chatCompletions?.usage?.promptTokensPath?.let {
            ctx.responseJsonPath.tryRead<Number>(it)
        } ?: 0

        val completionTokens = llmClientConfig.llmResponseConfig.chatCompletions?.usage?.completionTokensPath?.let {
            ctx.responseJsonPath.tryRead<Number>(it)
        } ?: 0

        val reasoningTokens = llmClientConfig.llmResponseConfig.chatCompletions?.usage?.reasoningTokensPath?.let {
            ctx.responseJsonPath.tryRead<Number>(it)
        } ?: 0

        val cacheReadTokens = llmClientConfig.llmResponseConfig.chatCompletions?.usage?.cacheReadTokensPath?.let {
            ctx.responseJsonPath.tryRead<Number>(it)
        } ?: 0

        val cacheWriteTokens = llmClientConfig.llmResponseConfig.chatCompletions?.usage?.cacheWriteTokensPath?.let {
            ctx.responseJsonPath.tryRead<Number>(it)
        } ?: 0

        return ChatResponse.Usage(
            promptTokens = promptTokens.toInt(),
            completionTokens = completionTokens.toInt(),
            reasoningTokens = reasoningTokens.toInt(),
            cachedPromptTokens = cacheReadTokens.toInt(),
            cacheCreationTokens = cacheWriteTokens.toInt(),
        )
    }

    protected open suspend fun executeRequest(chatRequest: ChatRequest): Response {
        return execute(
            buildRequest(
                getRequestUrl(llmClientConfig.chatCompletionPath),
                buildRequestBody(chatRequest)
            )
        )
    }

    private suspend fun execute(request: Request): Response {
        return try {
            withContext(Dispatchers.IO) {
                this@LLMClient.client.newCall(request).execute()
            }
        } catch (e: Exception) {
            throw RequestException("Request failed", e)
        }
    }

    /**
     * Adds the provider's auth header. Overridden where the scheme differs — the Messages API
     * authenticates with `x-api-key` rather than a bearer token.
     */
    protected open fun applyAuthHeader(builder: Request.Builder): Request.Builder {
        return builder.addHeader("Authorization", "Bearer ${llmClientConfig.apiKey}")
    }

    protected open fun buildRequest(url: String, requestBody: RequestBody): Request {
        return applyCustomHeaders(applyAuthHeader(Request.Builder().url(url)))
            .post(requestBody)
            .build()
    }

    protected open fun buildGetRequest(url: String): Request {
        return applyCustomHeaders(applyAuthHeader(Request.Builder().url(url)))
            .get()
            .build()
    }

    /**
     * Applies [LLMClientConfig.headers] last, replacing any header of the same name rather than
     * appending to it, so a configured header always wins over the one the client set.
     */
    protected fun applyCustomHeaders(builder: Request.Builder): Request.Builder {
        llmClientConfig.headers.forEach { (name, value) -> builder.header(name, value) }
        return builder
    }

    protected fun getRequestUrl(path: String): String {
        return llmClientConfig.normalizedBaseUrl + llmClientConfig.normalizedPath(path)
    }

    private fun buildRequestBody(chatRequest: ChatRequest): RequestBody {
        return this.transformRequestBody(chatRequest).toRequestBody("application/json".toMediaType())
    }


    private fun toChatResponseResolveContext(chatRequest: ChatRequest, responseBody: String): ChatResponseResolveContext {
        return ChatResponseResolveContext(
            chatRequest,
            responseBody,
            tryCast2StringMap(gson.fromJson(responseBody, Map::class.java))!!,
            JsonPath.parse(responseBody)
        )
    }

    protected fun tryCast2StringMap(obj: Any?): Map<String, Any?>? {
        if (obj == null || obj !is Map<*, *>) {
            return null
        }

        return obj.entries
            .filter { it.key is String }
            .associate { it.key as String to it.value }
    }

    protected fun tryCast2Iterator(obj: Any?): Iterator<Any?>? {
        if (obj == null) {
            return null
        }

        if (obj !is Collection<*> && obj !is Array<*>) {
            return emptyList<Any?>().iterator()
        }

        return when (obj) {
            is Collection<*> -> {
                obj.iterator()
            }

            is Array<*> -> {
                obj.iterator()
            }

            else -> emptyList<Any?>().iterator()
        }
    }

    protected fun tryCast2StringMapList(obj: Any?): List<Map<String, Any?>>? {
        val iterator = this.tryCast2Iterator(obj) ?: return null

        val results = mutableListOf<Map<String, Any?>>()

        for (item in iterator) {
            if (item == null || item !is Map<*, *>) {
                continue
            }

            val map = item.entries
                .filter { it.key is String }
                .associate { it.key as String to it.value }

            results.add(map)
        }

        return results
    }

    class ChatResponseResolveContext(
        val request: ChatRequest,
        val responseBody: String,
        val responseMap: Map<String, Any?>,
        val responseJsonPath: DocumentContext
    )

    companion object {
        private val SSE_DATA_FLAG = "data:"
        private val SSE_STOP_FLAG = "[DONE]"
    }
}