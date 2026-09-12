package com.lovelycatv.vertex.ai.llm.message

data class ToolCall(
    val index: Int,
    val id: String,
    val toolName: String,
    val arguments: Map<String, Any?>,
    val originalArguments: String,
    val streaming: Boolean,
)