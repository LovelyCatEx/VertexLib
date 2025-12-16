package com.lovelycatv.vertex.ai.openai

import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest
import com.lovelycatv.vertex.ai.openai.request.EmbeddingRequest
import com.lovelycatv.vertex.ai.openai.response.ChatCompletionResponse
import com.lovelycatv.vertex.ai.openai.response.EmbeddingResponse
import com.lovelycatv.vertex.ai.openai.response.ListModelsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * @author lovelycat
 * @since 2025-12-13 02:17
 * @version 1.0
 */
interface OpenApiService {
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("chat/completions")
    suspend fun chatCompletionBlocking(@Body request: ChatCompletionRequest): ChatCompletionResponse

    @Streaming
    @Headers("Accept: text/event-stream")
    @POST("chat/completions")
    fun chatCompletionStreaming(@Body request: ChatCompletionRequest): Call<ResponseBody>

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("embeddings")
    suspend fun embeddings(@Body request: EmbeddingRequest): EmbeddingResponse

    @GET("models")
    suspend fun listModels(): ListModelsResponse
}