package com.lovelycatv.vertex.ai.llm.tool

import com.lovelycatv.vertex.ai.llm.tool.parameter.ToolParameter

data class ToolDeclaration(
    val name: String,
    val description: String,
    val parameters: List<ToolParameter>,
)
