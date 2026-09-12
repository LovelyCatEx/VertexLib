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
        val response = this.executeRequest(chatRequest)

        val jsonResponse = withContext(Dispatchers.IO) {
            response.body?.string()
        } ?: throw RequestException("No response body")

        return this.resolveChatResponse(
            this.toChatResponseResolveContext(chatRequest, jsonResponse)
        )
    }

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
        val request = this.buildRequest(
            this.getRequestUrl(),
            this.buildRequestBody(chatRequest)
        )

        return try {
            withContext(Dispatchers.IO) {
                this@LLMClient.client.newCall(request).execute()
            }
        } catch (e: Exception) {
            throw RequestException("Request failed", e)
        }
    }


    protected open fun buildRequest(url: String, requestBody: RequestBody): Request {
        val builder = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${llmClientConfig.apiKey}")

        return applyCustomHeaders(builder).post(requestBody).build()
    }

    /**
     * Applies [LLMClientConfig.headers] last, replacing any header of the same name rather than
     * appending to it, so a configured header always wins over the one the client set.
     */
    protected fun applyCustomHeaders(builder: Request.Builder): Request.Builder {
        llmClientConfig.headers.forEach { (name, value) -> builder.header(name, value) }
        return builder
    }

    private fun getRequestUrl(): String {
        return llmClientConfig.normalizedBaseUrl + llmClientConfig.normalizedPath(llmClientConfig.chatCompletionPath)
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