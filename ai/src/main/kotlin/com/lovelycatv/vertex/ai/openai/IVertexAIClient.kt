package com.lovelycatv.vertex.ai.openai

import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest
import com.lovelycatv.vertex.ai.openai.request.EmbeddingRequest
import com.lovelycatv.vertex.ai.openai.response.ChatCompletionResponse
import com.lovelycatv.vertex.ai.openai.response.ChatCompletionStreamChunkResponse
import com.lovelycatv.vertex.ai.openai.response.EmbeddingResponse
import com.lovelycatv.vertex.ai.openai.response.ListModelsResponse
import kotlinx.coroutines.flow.Flow

/**
 * @author lovelycat
 * @since 2026-01-02 20:13
 * @version 1.0
 */
interface IVertexAIClient {
    suspend fun chatCompletionBlocking(request: ChatCompletionRequest): ChatCompletionResponse

    fun chatCompletionStreaming(request: ChatCompletionRequest) : Flow<ChatCompletionStreamChunkResponse>

    suspend fun embeddings(request: EmbeddingRequest): EmbeddingResponse

    suspend fun listModels(): ListModelsResponse

}