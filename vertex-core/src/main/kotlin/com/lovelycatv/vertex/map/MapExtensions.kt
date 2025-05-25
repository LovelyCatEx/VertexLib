package com.lovelycatv.vertex.map

/**
 * @author lovelycat
 * @since 2025-05-25 20:14
 * @version 1.0
 */
class MapExtensions private constructor()

fun <K: Any, V> Map<K, V>.toWrappedMap(): WrappedMap<K, V> {
    return WrappedMap(this)
}

fun <K : Any, V> WrappedMap<K, V>.toMutableWrappedMap(): MutableWrappedMap<K, V> {
    return MutableWrappedMap(this.map)
}