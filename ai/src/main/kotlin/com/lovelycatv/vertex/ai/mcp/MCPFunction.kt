package com.lovelycatv.vertex.ai.mcp

/**
 * @author lovelycat
 * @since 2025-12-16 20:15
 * @version 1.0
 */
data class MCPFunction(
    val name: String,
    val description: String? = null,
    val inputSchema: InputSchema
) {
    data class InputSchema(
        val type: String,
        val properties: Map<String, Property>,
        val required: List<String>?
    ) {
        sealed class Property {
            abstract val type: String
            abstract val description: String?

            data class StringProperty(
                override val type: String = "string",
                override val description: String? = null,
                val enum: List<String>? = null
            ) : Property()

            data class IntegerProperty(
                override val type: String = "integer",
                override val description: String? = null,
                val enum: List<Int>? = null
            ) : Property()

            data class NumberProperty(
                override val type: String = "number",
                override val description: String? = null,
                val enum: List<Double>? = null
            ) : Property()

            data class BooleanProperty(
                override val type: String = "boolean",
                override val description: String? = null
            ) : Property()

            data class ObjectProperty(
                override val type: String = "object",
                override val description: String? = null,
                val properties: Map<String, Property>? = null,
                val required: List<String>? = null
            ) : Property()

            data class ArrayProperty(
                override val type: String = "array",
                override val description: String? = null,
                val items: Property
            ) : Property()

            data class NullProperty(
                override val type: String = "null",
                override val description: String? = null
            ) : Property()
        }
    }
}
