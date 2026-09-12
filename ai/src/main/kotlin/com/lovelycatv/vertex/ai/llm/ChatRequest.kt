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
    /**
     * Raw fields merged into the request body after every typed field above, so an entry here
     * overrides whatever the client derived — the equivalent of `{...body, ...extraBody}` in
     * TypeScript. Provider-specific knobs that [ChatRequest] does not model go here.
     *
     * A key mapped to null drops the field from the body entirely rather than sending `null`,
     * which is also how a derived field can be turned back off.
     */
    val extraBody: Map<String, Any?>? = null,
)