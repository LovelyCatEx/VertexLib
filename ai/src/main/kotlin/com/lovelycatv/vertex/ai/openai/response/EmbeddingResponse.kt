package com.lovelycatv.vertex.ai.openai.response

import com.google.gson.annotations.SerializedName

/**
 * @author lovelycat
 * @since 2025-12-16 13:24
 * @version 1.0
 */
data class EmbeddingResponse(
    @SerializedName("object")
    val obj: String,
    val model: String,
    val data: List<DataItem>,
    val usage: Usage
) {
    data class DataItem(
        @SerializedName("object")
        val obj: String,
        val embedding: List<Float>,
        val index: Int
    )

    data class Usage(
        @SerializedName("prompt_tokens")
        val promptTokens: Int,
        @SerializedName("completion_tokens")
        val completionTokens: Int,
        @SerializedName("total_tokens")
        val totalTokens: Int
    )
}