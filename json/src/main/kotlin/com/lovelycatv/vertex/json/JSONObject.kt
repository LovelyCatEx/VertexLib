package com.lovelycatv.vertex.json

import com.lovelycatv.vertex.json.exception.JsonNodeAccessException

/**
 * @author lovelycat
 * @since 2025-08-01 00:23
 * @version 1.0
 */
class JSONObject(private val map: MutableMap<String, Any?> = mutableMapOf()) : MutableMap<String, Any?> by map, JSONNode {

    override val size: Int get() = this.map.keys.size

    override val elementSize: Int get() = this.size

    companion object {
        private val parser = JSONParser()
        fun parse(jsonStr: String): JSONObject {
            return JSONObject(parser.initialize(jsonStr).parseObject().toMutableMap())
        }
    }

    fun getJSONObject(key: String): JSONObject? {
        return when (val obj = this[key]) {
            is JSONObject -> obj
            is Map<*, *> -> {
                if (obj.keys.any { it != null && it::class == String::class }) {
                    @Suppress("UNCHECKED_CAST")
                    JSONObject((obj as Map<String, Any?>).toMutableMap())
                } else {
                    throw JsonNodeAccessException(this, key, Map::class.java, obj::class.java)
                }
            }
            null -> null
            else -> throw JsonNodeAccessException(this, key, Map::class.java, obj::class.java)
        }
    }

    fun getJSONArray(key: String): JSONArray? {
        return when (val obj = this[key]) {
            is JSONArray -> obj
            is Collection<*> -> JSONArray(obj)
            null -> null
            else -> throw JsonNodeAccessException(this, key, Collection::class.java, obj::class.java)
        }
    }

    fun getString(key: String): String? {
        return when (val obj = this[key]) {
            is CharSequence -> {
                obj.toString()
            }
            null -> null
            else -> throw JsonNodeAccessException(this, key, String::class.java, obj::class.java)
        }
    }

    fun getShort(key: String): Short? {
        return when (val obj = this[key]) {
            is Short -> obj
            null -> null
            else -> throw JsonNodeAccessException(this, key, Short::class.java, obj::class.java)
        }
    }

    fun getInteger(key: String): Int? {
        return when (val obj = this[key]) {
            is Int -> obj
            null -> null
            else -> throw JsonNodeAccessException(this, key, Int::class.java, obj::class.java)
        }
    }

    fun getLong(key: String): Long? {
        return when (val obj = this[key]) {
            is Long -> obj
            null -> null
            else -> throw JsonNodeAccessException(this, key, Long::class.java, obj::class.java)
        }
    }

    fun getFloat(key: String): Float? {
        return when (val obj = this[key]) {
            is Float -> obj
            null -> null
            else -> throw JsonNodeAccessException(this, key, Float::class.java, obj::class.java)
        }
    }

    fun getDouble(key: String): Double? {
        return when (val obj = this[key]) {
            is Double -> obj
            null -> null
            else -> throw JsonNodeAccessException(this, key, Double::class.java, obj::class.java)
        }
    }

    override fun toJSONString(): String {
        return this.map.toList().joinToString(separator = ", ", prefix = "{", postfix = "}") {
            val key = it.first
            "\"$key\": " + when (val value = it.second) {
                is JSONNode -> value.toJSONString()
                is Map<*, *> -> this.getJSONObject(it.first)!!.toJSONString()
                is Collection<*> -> this.getJSONArray(it.first)!!.toJSONString()
                null -> "null"
                is CharSequence -> "\"$value\""
                else -> value.toString()
            }
        }
    }
}