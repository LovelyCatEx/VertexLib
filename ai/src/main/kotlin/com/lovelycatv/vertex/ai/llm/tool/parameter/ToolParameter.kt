package com.lovelycatv.vertex.ai.llm.tool.parameter

sealed class ToolParameter(
    val type: ToolParameterType,
    val name: String,
    val description: String,
)