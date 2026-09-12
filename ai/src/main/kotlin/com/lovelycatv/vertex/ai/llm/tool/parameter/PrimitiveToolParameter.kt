package com.lovelycatv.vertex.ai.llm.tool.parameter

class PrimitiveToolParameter(
    type: ToolParameterType,
    name: String,
    description: String,
    override val required: Boolean,
) : ToolParameter(type, name, description), RequirableToolParameter {
    init {
        check(type in ToolParameterType.PRIMITIVE_TYPES) {
            "Parameter type must be one of PRIMITIVE_TYPES: [${ToolParameterType.PRIMITIVE_TYPES.joinToString()}]"
        }
    }
}