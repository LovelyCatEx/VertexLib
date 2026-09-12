package com.lovelycatv.vertex.ai.llm.message

class SystemChatMessage(
    override val content: String
) : ChatMessage {
    override val type = ChatMessageType.SYSTEM
}