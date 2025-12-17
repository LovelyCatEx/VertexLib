package com.lovelycatv.vertex.ai.workflow.graph.node

import com.google.gson.Gson
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-12-17 00:00
 * @version 1.0
 */
data class GraphNodeParameter(
    val type: KClass<*>,
    val name: String
) {
    companion object {
        private val aliasTypeMap: Map<String, Class<*>> = mapOf(
            "int" to Int::class.java,
            "kotlin.Int" to Int::class.java,
            "java.lang.Integer" to Int::class.java,

            "boolean" to Boolean::class.java,
            "kotlin.Boolean" to Boolean::class.java,
            "java.lang.Boolean" to Boolean::class.java,

            "long" to Long::class.java,
            "kotlin.Long" to Long::class.java,
            "java.lang.Long" to Long::class.java,

            "double" to Double::class.java,
            "kotlin.Double" to Double::class.java,
            "java.lang.Double" to Double::class.java,

            "float" to Float::class.java,
            "kotlin.Float" to Float::class.java,
            "java.lang.Float" to Float::class.java,

            "char" to Char::class.java,
            "kotlin.Char" to Char::class.java,
            "java.lang.Character" to Char::class.java,

            "byte" to Byte::class.java,
            "kotlin.Byte" to Byte::class.java,
            "java.lang.Byte" to Byte::class.java,

            "short" to Short::class.java,
            "kotlin.Short" to Short::class.java,
            "java.lang.Short" to Short::class.java
        )

        fun fromSerialized(serialized: String, gson: Gson = Gson()): GraphNodeParameter {
            return this.fromSerialized(gson.fromJson<Map<String, String>>(serialized, Map::class.java))
        }

        fun fromSerialized(serialized: Map<String, String>): GraphNodeParameter {
            return GraphNodeParameter(
                type = (aliasTypeMap[serialized["type"]!!]
                    ?: Class.forName(serialized["type"]!!)).kotlin,
                name = serialized["name"]!!
            )
        }
    }

    fun serialize(): Map<String, String> {
        return mapOf(
            "type" to type.java.canonicalName,
            "name" to name
        )
    }
}