package com.lovelycatv.vertex.ai.llm.message

class ToolChatMessage(
    val toolCallId: String,
    override val content: String
) : ChatMessage {
    override val type = ChatMessageType.TOOL
}