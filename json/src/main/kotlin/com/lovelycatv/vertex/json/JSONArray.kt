package com.lovelycatv.vertex.json

import com.lovelycatv.vertex.json.exception.JsonIndexOutOfBoundException
import com.lovelycatv.vertex.json.exception.JsonNodeAccessException

/**
 * @author lovelycat
 * @since 2025-08-01 00:26
 * @version 1.0
 */
class JSONArray(private val list: MutableList<Any?> = mutableListOf()) : MutableList<Any?> by list, JSONNode {
    constructor(list: Collection<Any?>) : this(list.toMutableList())

    override val size: Int get() = this.list.size

    override val elementSize: Int get() = this.size

    companion object {
        private val parser = JSONParser()
        fun parse(jsonStr: String): JSONArray {
            return JSONArray(parser.initialize(jsonStr).parseArray())
        }
    }

    fun getJSONObject(index: Int): JSONObject? {
        this.checkIndexInBound(index)

        return when (val obj = this[index]) {
            is JSONObject -> obj
            is Map<*, *> -> {
                if (obj.keys.any { it != null && it::class == String::class }) {
                    @Suppress("UNCHECKED_CAST")
                    JSONObject((obj as Map<String, Any?>).toMutableMap())
                } else {
                    throw JsonNodeAccessException(this, index, Map::class.java, obj::class.java)
                }
            }
            null -> null
            else -> throw JsonNodeAccessException(this, index, Map::class.java, obj::class.java)
        }
    }

    fun getJSONArray(index: Int): JSONArray? {
        this.checkIndexInBound(index)

        return when (val obj = this[index]) {
            is JSONArray -> obj
            is Collection<*> -> JSONArray(obj)
            null -> null
            else -> throw JsonNodeAccessException(this, index, Collection::class.java, obj::class.java)
        }
    }

    fun getString(index: Int): String? {
        this.checkIndexInBound(index)

        return when (val obj = this[index]) {
            is CharSequence -> {
                obj.toString()
            }
            null -> null
            else -> throw JsonNodeAccessException(this, index, CharSequence::class.java, obj::class.java)
        }
    }

    fun getShort(index: Int): Short? {
        this.checkIndexInBound(index)

        return when (val obj = this[index]) {
            is Short -> obj
            null -> null
            else -> throw JsonNodeAccessException(this, index, Short::class.java, obj::class.java)
        }
    }

    fun getInteger(index: Int): Int? {
        this.checkIndexInBound(index)

        return when (val obj = this[index]) {
            is Int -> obj
            null -> null
            else -> throw JsonNodeAccessException(this, index, Int::class.java, obj::class.java)
        }
    }

    fun getLong(index: Int): Long? {
        this.checkIndexInBound(index)

        return when (val obj = this[index]) {
            is Long -> obj
            null -> null
            else -> throw JsonNodeAccessException(this, index, Long::class.java, obj::class.java)
        }
    }

    fun getFloat(index: Int): Float? {
        this.checkIndexInBound(index)

        return when (val obj = this[index]) {
            is Float -> obj
            null -> null
            else -> throw JsonNodeAccessException(this, index, Float::class.java, obj::class.java)
        }
    }

    fun getDouble(index: Int): Double? {
        this.checkIndexInBound(index)

        return when (val obj = this[index]) {
            is Double -> obj
            null -> null
            else -> throw JsonNodeAccessException(this, index, Double::class.java, obj::class.java)
        }
    }

    private fun checkIndexInBound(index: Int) {
        if (index >= this.size) {
            throw JsonIndexOutOfBoundException(this, index)
        }
    }

    override fun toJSONString(): String {
        return this.list.joinToString(separator = ", ", prefix = "[", postfix = "]") {
            val t = when (val value = it) {
                is JSONNode -> value.toJSONString()
                is Collection<*> -> JSONArray(value).toJSONString()
                is Map<*, *> -> JSONObject(value.mapKeys { it.key.toString() }.toMutableMap()).toJSONString()
                null -> "null"
                is CharSequence -> "\"$value\""
                else -> value.toString()
            }

            t
        }
    }
}