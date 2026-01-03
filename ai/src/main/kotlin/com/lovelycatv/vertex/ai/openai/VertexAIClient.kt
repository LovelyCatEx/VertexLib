package com.lovelycatv.vertex.ai.openai

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.network.VertexRetrofit
import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest
import com.lovelycatv.vertex.ai.openai.request.EmbeddingRequest
import com.lovelycatv.vertex.ai.openai.response.ChatCompletionResponse
import com.lovelycatv.vertex.ai.openai.response.ChatCompletionStreamChunkResponse
import com.lovelycatv.vertex.ai.openai.response.EmbeddingResponse
import com.lovelycatv.vertex.ai.openai.response.ListModelsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * @author lovelycat
 * @since 2025-12-13 02:14
 * @version 1.0
 */
class VertexAIClient(
    val config: VertexAIClientConfig,
    val gson: Gson = Gson()
) : IVertexAIClient {
    private val retrofit = VertexRetrofit(
        baseUrl = this.config.baseUrl,
        timeoutSeconds = this.config.timeoutSeconds,
        enableLogging = this.config.enableLogging,
        preInterceptor = { chain ->
            val originalRequest = chain.request()
            val requestWithAuth = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer ${this.config.apiKey}")
                .build()
            chain.proceed(requestWithAuth)
        }
    ).retrofit

    private val apiService: OpenApiService = retrofit.create(OpenApiService::class.java)

    override suspend fun chatCompletionBlocking(request: ChatCompletionRequest): ChatCompletionResponse {
        request.validateParameters()

        return apiService.chatCompletionBlocking(request)
    }

    override fun chatCompletionStreaming(request: ChatCompletionRequest) : Flow<ChatCompletionStreamChunkResponse> {
        request.validateParameters()

        val call = apiService.chatCompletionStreaming(request.copy(stream = true))

        return flow {
            val response = try {
                call.execute()
            } catch (e: Exception) {
                throw e
            }

            if (!response.isSuccessful) {
                throw IllegalStateException("Request failed")
            }

            val body = response.body() ?: throw IllegalStateException("empty response body")

            body.byteStream().bufferedReader().use { reader ->
                while (true) {
                    val line = reader.readLine() ?: break

                    if (line.isBlank()) continue

                    if (line.startsWith("data: ")) {
                        val data = line.substring(6)

                        if (data == "[DONE]") {
                            break
                        }

                        emit(
                            this@VertexAIClient.gson.fromJson(line.replace("data:", "").trim(),
                                ChatCompletionStreamChunkResponse::class.java)
                        )
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun embeddings(request: EmbeddingRequest): EmbeddingResponse {
        return this.apiService.embeddings(request)
    }

    override suspend fun listModels(): ListModelsResponse {
        return this.apiService.listModels()
    }

}