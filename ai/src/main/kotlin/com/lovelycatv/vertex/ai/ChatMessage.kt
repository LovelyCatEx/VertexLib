package com.lovelycatv.vertex.ai

import com.google.gson.annotations.SerializedName

/**
 * @author lovelycat
 * @since 2025-12-13 02:01
 * @version 1.0
 */
class ChatMessage(
    val role: ChatMessageRole,
    val content: String,
    @SerializedName("tool_calls")
    val toolCalls: List<ToolCall>? = null
) {
    data class ToolCall(
        val id: String,
        val type: String,
        val function: Function,
    ) {
        data class Function(
            val name: String,
            val arguments: String
        )
    }
}