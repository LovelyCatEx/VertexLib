package com.lovelycatv.vertex.ai.openai.message

import com.google.gson.annotations.SerializedName
import com.lovelycatv.vertex.ai.openai.ChatMessageRole

data class DeltaMessage(
    override val role: ChatMessageRole?,
    override val content: String?,
    @SerializedName("tool_calls")
    val toolCalls: List<ToolCall>? = null
) : IChatMessage {
    data class ToolCall(
        val index: Int,
        val id: String?,
        val type: String?,
        val function: Function?,
    ) {
        data class Function(
            val name: String,
            val arguments: String
        )
    }
}