package com.lovelycatv.vertex.ai.openai.message

import com.lovelycatv.vertex.ai.openai.ChatMessageRole

/**
 * @author lovelycat
 * @since 2025-12-13 02:01
 * @version 1.0
 */
data class ChatMessage(
    override val role: ChatMessageRole,
    override val content: String,
) : IChatMessage {
    override val reasoningContent: String? = null

    companion object {
        fun system(content: String): ChatMessage {
            return ChatMessage(ChatMessageRole.SYSTEM, content)
        }

        fun user(content: String): ChatMessage {
            return ChatMessage(ChatMessageRole.USER, content)
        }

        fun assistant(content: String): ChatMessage {
            return ChatMessage(ChatMessageRole.ASSISTANT, content)
        }

        fun tool(content: String): ChatMessage {
            return ChatMessage(ChatMessageRole.TOOL, content)
        }
    }
}