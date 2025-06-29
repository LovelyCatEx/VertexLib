package com.lovelycatv.vertex.cache.provider

import com.lovelycatv.vertex.cache.store.InMemoryKVStore

/**
 * @author lovelycat
 * @since 2025-06-29 10:04
 * @version 1.0
 */
class InMemoryCacheSourceProvider<K: CharSequence, V> : CacheSourceProvider<K, V> {
    private val inMemoryKVStore = InMemoryKVStore<K, V>()

    override val keys: Set<K>
        get() = this.inMemoryKVStore.keys
    override val size: Int
        get() = this.inMemoryKVStore.size

    override fun get(key: K): V? {
        return this.inMemoryKVStore[key]
    }

    override fun set(key: K, value: V, expiration: Long) {
        this.inMemoryKVStore.set(key, value, expiration)
    }

    override fun set(key: K, value: V) {
        this.inMemoryKVStore[key] = value
    }

    override fun clear() {
        this.inMemoryKVStore.clear()
    }

    override fun containsKey(key: K): Boolean {
        return this.inMemoryKVStore.containsKey(key)
    }

    override fun remove(key: K): V? {
        return this.inMemoryKVStore.remove(key)
    }
}