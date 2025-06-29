package com.lovelycatv.vertex.cache.key

/**
 * @author lovelycat
 * @since 2025-06-28 19:05
 * @version 1.0
 */
class FixedCacheKey<K: CharSequence, V>(
    private val fixedKey: K
) : CacheKey<K, V> {
    override fun getKey(vararg args: Any?): K {
        return this.fixedKey
    }

    override fun getKeyForSetValue(data: V): K {
        return this.fixedKey
    }
}