package com.lovelycatv.vertex.cache.store

/**
 * @author lovelycat
 * @since 2025-06-28 09:05
 * @version 1.0
 */
interface ExpiringKVStore<K: Any, V> : KVStore<K, V> {
    fun set(key: K, value: V, expiration: Long)
}