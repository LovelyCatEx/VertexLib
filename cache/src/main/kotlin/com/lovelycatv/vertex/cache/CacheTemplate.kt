package com.lovelycatv.vertex.cache

import com.lovelycatv.vertex.cache.key.CacheKey
import com.lovelycatv.vertex.cache.provider.CacheSourceProvider
import com.lovelycatv.vertex.cache.provider.DataSourceProvider
import com.lovelycatv.vertex.cache.store.ExpiringKVStore

/**
 * @author lovelycat
 * @since 2025-06-28 11:39
 * @version 1.0
 */
abstract class CacheTemplate<K: CharSequence, V>(
    private val cacheSourceProvider: CacheSourceProvider<K, V>,
    private val dataSourceProvider: DataSourceProvider<K, V>
) : ExpiringKVStore<K, V> by cacheSourceProvider {
    private val _cacheKeys: MutableMap<Int, CacheKey<K, V>> = mutableMapOf()
    val cacheKeys: Map<Int, CacheKey<K, V>> get() = this._cacheKeys

    fun addCacheKey(id: Int, cacheKey: CacheKey<K, V>) {
        this._cacheKeys[id] = cacheKey
    }

    fun addCacheKeys(keys: Map<Int, CacheKey<K, V>>) {
        this._cacheKeys.putAll(keys)
    }

    fun addCacheKeys(vararg keys: Pair<Int, CacheKey<K, V>>) {
        this.addCacheKeys(keys.toMap())
    }

    fun get(id: Int, vararg args: Any?): V? {
        val cacheKey = this.cacheKeys[id] ?: throw IllegalArgumentException("Unknown key id: $id.")
        val realKey = cacheKey.getKey(args)
        return this[realKey] ?: dataSourceProvider.provide(id, *args)
    }

    fun set(id: Int, data: V) {
        val cacheKey = this.cacheKeys[id] ?: throw IllegalArgumentException("Unknown key id: $id.")
        val realKey = cacheKey.getKeyForSetValue(data)
        this[realKey] = data
    }

    fun set(id: Int, data: V, expiration: Long) {
        val cacheKey = this.cacheKeys[id] ?: throw IllegalArgumentException("Unknown key id: $id.")
        val realKey = cacheKey.getKeyForSetValue(data)
        this.set(realKey, data, expiration)
    }
}