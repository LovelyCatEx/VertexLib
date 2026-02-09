package com.lovelycatv.vertex.ai.openai.message

import com.lovelycatv.vertex.ai.openai.ChatMessageRole

/**
 * @author lovelycat
 * @since 2025-12-13 04:09
 * @version 1.0
 */
interface IChatMessage {
    val role: ChatMessageRole?
    val content: String?
    val reasoningContent: String?
}