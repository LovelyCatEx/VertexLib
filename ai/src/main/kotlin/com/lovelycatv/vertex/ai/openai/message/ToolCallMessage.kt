package com.lovelycatv.vertex.ai.openai.message

import com.google.gson.annotations.SerializedName
import com.lovelycatv.vertex.ai.openai.ChatMessageRole

data class ToolCallMessage(
    override val role: ChatMessageRole?,
    override val content: String?,
    @SerializedName("tool_call_id")
    val toolCallId: String
) : IChatMessage {
    override val reasoningContent: String? get() = null
}