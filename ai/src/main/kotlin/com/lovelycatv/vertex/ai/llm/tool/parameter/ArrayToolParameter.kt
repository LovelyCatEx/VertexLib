package com.lovelycatv.vertex.ai.llm.tool.parameter

class ArrayToolParameter(
    name: String,
    description: String,
    val itemType: ToolParameter,
    override val required: Boolean,
) : ToolParameter(ToolParameterType.ARRAY, name, description), RequirableToolParameter {
    init {
        check(type == ToolParameterType.ARRAY) {
            "Parameter type must be ${ToolParameterType::class.qualifiedName}.${ToolParameterType.ARRAY.name}"
        }
    }
}