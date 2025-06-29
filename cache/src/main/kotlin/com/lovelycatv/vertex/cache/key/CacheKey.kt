package com.lovelycatv.vertex.cache.key

/**
 * @author lovelycat
 * @since 2025-06-28 18:52
 * @version 1.0
 */
interface CacheKey<K: CharSequence, V> {
    fun getKey(vararg args: Any?): K

    fun getKeyForSetValue(data: V): K
}