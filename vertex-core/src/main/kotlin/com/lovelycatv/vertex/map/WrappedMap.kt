package com.lovelycatv.vertex.map

/**
 * @author lovelycat
 * @since 2025-05-25 19:51
 * @version 1.0
 */
open class WrappedMap<K: Any, V: Any?>() : Map<K, V>, MutableWrappedMap<K, V>() {
    constructor(map: Map<K, V>) : this() {
        super.putAll(map)
    }

    override fun toString(): String {
        return super.toString()
    }
}