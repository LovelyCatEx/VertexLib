package com.lovelycatv.vertex.ai.llm.message

enum class StopReason {
    END_TURN,
    REACHED_MAX_TOKENS,
    TOOL_CALLS,
    CONTENT_FILTER,
    OTHER;
}