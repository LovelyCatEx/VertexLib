package com.lovelycatv.vertex.cache.store

/**
 * @author lovelycat
 * @since 2025-06-28 08:59
 * @version 1.0
 */
interface KVStore<K: Any, V> {
    val keys: Set<K>

    val size: Int

    operator fun get(key: K): V?

    operator fun set(key: K, value: V)

    fun remove(key: K): V?

    fun containsKey(key: K): Boolean

    fun clear()
}