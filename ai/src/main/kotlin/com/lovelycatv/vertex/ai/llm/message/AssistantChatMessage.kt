package com.lovelycatv.vertex.ai.llm.message

class AssistantChatMessage(
    override val content: String?,
    val reasoningContent: String?,
    val toolCalls: List<ToolCall>?,
    val stopReason: StopReason?,
    val stopReasonString: String?,
) : ChatMessage {
    override val type = ChatMessageType.ASSISTANT

    val hasToolCall: Boolean get() = toolCalls?.isNotEmpty() ?: false

    val hasValidToolCall: Boolean get() = toolCalls?.isNotEmpty() == true && toolCalls.any { !it.streaming }
}