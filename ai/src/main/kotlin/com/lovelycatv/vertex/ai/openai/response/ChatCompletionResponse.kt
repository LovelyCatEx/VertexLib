package com.lovelycatv.vertex.ai.openai.response

import com.google.gson.annotations.SerializedName
import com.lovelycatv.vertex.ai.openai.message.ChoiceMessage

/**
 * @author lovelycat
 * @since 2025-12-13 02:37
 * @version 1.0
 */
class ChatCompletionResponse(
    id: String,
    created: Int,
    model: String,
    systemFingerprint: String,
    obj: String,
    val choices: List<Choice>,
    val usage: ChatCompletionResponseUsage
) : AbstractChatCompletionResponse(id, created, model, systemFingerprint, obj) {
    data class Choice(
        val index: Int,
        @SerializedName("finish_reason")
        val finishReason: String,
        val message: ChoiceMessage,
        val logprobs: Logprobs? = null
    )
}