package com.lovelycatv.vertex.ai.openai.response

import com.google.gson.annotations.SerializedName

/**
 * @author lovelycat
 * @since 2025-12-13 02:29
 * @version 1.0
 */
abstract class AbstractChatCompletionResponse(
    val id: String,
    val created: Int,
    val model: String,
    @SerializedName("system_fingerprint")
    val systemFingerprint: String,
    @SerializedName("object")
    val obj: String
) {
    /**
     * @author lovelycat
     * @since 2025-12-13 02:37
     * @version 1.0
     */
    data class Usage(
        @SerializedName("completion_tokens")
        val completionTokens: Int,
        @SerializedName("prompt_tokens")
        val promptTokens: Int,
        @SerializedName("total_tokens")
        val totalTokens: Int
    )
}