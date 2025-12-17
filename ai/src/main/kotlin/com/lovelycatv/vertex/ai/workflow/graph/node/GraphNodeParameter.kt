package com.lovelycatv.vertex.ai.workflow.graph.node

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.utils.ReflectUtils
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
        fun fromSerialized(serialized: String, gson: Gson = Gson()): GraphNodeParameter {
            return this.fromSerialized(gson.fromJson<Map<String, String>>(serialized, Map::class.java))
        }

        fun fromSerialized(serialized: Map<String, String>): GraphNodeParameter {
            return GraphNodeParameter(
                type = ReflectUtils.classForNameIncludingPrimitiveTypes(serialized["type"]!!).kotlin,
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