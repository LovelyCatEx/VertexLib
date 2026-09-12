package com.lovelycatv.vertex.ai.llm.message

class UserChatMessage(
    override val content: String
) : ChatMessage {
    override val type = ChatMessageType.USER
}