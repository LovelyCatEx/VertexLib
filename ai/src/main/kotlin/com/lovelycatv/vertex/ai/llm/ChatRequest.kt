package com.lovelycatv.vertex.ai.llm

import com.lovelycatv.vertex.ai.llm.message.ChatMessage
import com.lovelycatv.vertex.ai.llm.tool.ToolDeclaration

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val stream: Boolean,
    val tools: List<ToolDeclaration>? = null,
    val reasoningEffort: ReasoningEffort = ReasoningEffort.DISABLED,
    val maxCompletionTokens: Int? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val presencePenalty: Float? = null,
    val frequencyPenalty: Float? = null,
    val logprobs: Boolean? = null,
    val topLogprobs: Int? = null,
)