package com.lovelycatv.vertex.ai.llm.tool.parameter

class ObjectToolParameter(
    name: String,
    description: String,
    val properties: List<ToolParameter>,
    val strict: Boolean,
    val additionalProperties: Boolean,
) : ToolParameter(ToolParameterType.OBJECT, name, description) {
    init {
        check(type == ToolParameterType.OBJECT) {
            "Parameter type must be ${ToolParameterType::class.qualifiedName}.${ToolParameterType.ARRAY.name}"
        }
    }
}