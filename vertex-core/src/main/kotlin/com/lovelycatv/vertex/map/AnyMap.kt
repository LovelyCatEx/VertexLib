package com.lovelycatv.vertex.map

/**
 * @author lovelycat
 * @since 2024-10-27 20:19
 * @version 1.0
 */
open class AnyMap<K> : WrappedMap<K, Any?>() {
    fun getString(key: K): String? = this[key] as? String

    fun getShort(key: K): Short? = this[key] as? Short

    fun getInt(key: K): Int? = this[key] as? Int

    fun getLong(key: K): Long? = this[key] as? Long

    fun getFloat(key: K): Float? = this[key] as? Float

    fun getDouble(key: K): Double? = this[key] as? Double

    fun getByte(key: K): Byte? = this[key] as? Byte
}