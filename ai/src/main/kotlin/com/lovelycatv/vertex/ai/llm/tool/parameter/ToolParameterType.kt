package com.lovelycatv.vertex.ai.llm.tool.parameter

enum class ToolParameterType {
    STRING,
    INTEGER,
    NUMBER,
    BOOLEAN,
    ARRAY,
    OBJECT;

    companion object {
        val PRIMITIVE_TYPES = arrayOf(STRING, INTEGER, BOOLEAN, NUMBER)
    }
}