package com.lovelycatv.vertex.ai.response

import com.google.gson.annotations.SerializedName
import com.lovelycatv.vertex.ai.ChatMessageRole

/**
 * @author lovelycat
 * @since 2025-12-13 02:52
 * @version 1.0
 */
class ChatCompletionStreamChunkResponse(
    id: String,
    created: Int,
    model: String,
    systemFingerprint: String,
    obj: String,
    val choices: List<Choice>,
    val usage: ChatCompletionResponseUsage? = null
) : AbstractChatCompletionResponse(id, created, model, systemFingerprint, obj) {
    data class Choice(
        val index: Int,
        val delta: Delta,
        @SerializedName("finish_reason")
        val finishReason: String,
        val logprobs: Logprobs? = null
    ) {
        data class Delta(
            val role: ChatMessageRole,
            val content: String
        )
    }
}