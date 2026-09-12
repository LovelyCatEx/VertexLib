package com.lovelycatv.vertex.ai.llm.message

interface ChatMessage {
    val type: ChatMessageType
    val content: String?
}