package com.lovelycatv.vertex.ai

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.request.ChatCompletionRequest
import com.lovelycatv.vertex.ai.response.ChatCompletionResponse
import com.lovelycatv.vertex.ai.response.ChatCompletionStreamChunkResponse
import com.lovelycatv.vertex.ai.response.ListModelsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author lovelycat
 * @since 2025-12-13 02:14
 * @version 1.0
 */
class VertexAIClient(
    baseUrl: String,
    apiKey: String,
    timeoutSeconds: Long = 60,
    enableLogging: Boolean = true,
    private val gson: Gson = Gson()
) {
    private var apiService: OpenApiService

    init {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestWithAuth = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(requestWithAuth)
            }

        if (enableLogging) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            clientBuilder.addInterceptor(loggingInterceptor)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        this.apiService = retrofit.create(OpenApiService::class.java)
    }

    suspend fun chatCompletionBlocking(request: ChatCompletionRequest): ChatCompletionResponse {
        request.validateParameters()

        return apiService.chatCompletionBlocking(request)
    }

    fun chatCompletionStreaming(request: ChatCompletionRequest) : Flow<ChatCompletionStreamChunkResponse> {
        request.validateParameters()

        val call = apiService.chatCompletionStreaming(request)

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
                            gson.fromJson(line.replace("data:", "").trim(),
                                ChatCompletionStreamChunkResponse::class.java)
                        )
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun listModels(): ListModelsResponse {
        return this.apiService.listModels()
    }

}