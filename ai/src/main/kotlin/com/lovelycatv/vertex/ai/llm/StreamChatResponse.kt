package com.lovelycatv.vertex.ai.llm

open class StreamChatResponse(
    id: String,
    timestamp: Long,
    model: String,
    choices: List<Choice>,
    usage: Usage,
    val finished: Boolean,
    originalResponse: String
) : ChatResponse(true, id, timestamp, model, choices, usage, originalResponse)