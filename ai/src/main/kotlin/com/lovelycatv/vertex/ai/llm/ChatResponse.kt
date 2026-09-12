package com.lovelycatv.vertex.ai.llm

import com.lovelycatv.vertex.ai.llm.message.AssistantChatMessage


open class ChatResponse(
    val success: Boolean,
    val id: String,
    val timestamp: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage,
    val originalResponse: String
) {
    data class Choice(
        val index: Int,
        val message: AssistantChatMessage
    )

    data class Usage(
        val promptTokens: Int = 0,
        val completionTokens: Int = 0,
        val reasoningTokens: Int = 0,
        val cachedPromptTokens: Int = 0,
        val cacheCreationTokens: Int = 0,
    )
}