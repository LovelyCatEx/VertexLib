package com.lovelycatv.vertex.cache.store

import kotlinx.coroutines.*
import kotlin.math.ceil

/**
 * @author lovelycat
 * @since 2025-06-28 09:13
 * @version 1.0
 */
class InMemoryKVStore<K: Any, V>(
    private val expirationKeysCheckDelay: Long = 10 * 1000L
) : ExpiringKVStore<K, V> {
    private val expirationCheckCoroutineScope = CoroutineScope(
        Dispatchers.IO + CoroutineName("ExpirationCheck")
    )

    init {
        expirationCheckCoroutineScope.launch {
            while (true) {
                val expiringKeys = this@InMemoryKVStore._expirationMap.keys.toList()
                val expiringKeysCount = expiringKeys.size
                for (i in 0..<ceil(expiringKeysCount / 2.0).toInt()) {
                    val key = expiringKeys[i]
                    if (this@InMemoryKVStore.isKeyExpired(key)) {
                        this@InMemoryKVStore.remove(key)
                    }
                }
                delay(expirationKeysCheckDelay)
            }
        }
    }

    /**
     * Key with (startTime, expiration)
     */
    private val _expirationMap = mutableMapOf<K, Pair<Long, Long>>()

    private val _map = mutableMapOf<K, V>()

    override val keys: Set<K>
        get() = this._map.keys.filter { !this.isKeyExpired(it) }.toSet()
    override val size: Int
        get() = this.keys.size

    override fun set(key: K, value: V, expiration: Long) {
        this._expirationMap[key] = System.currentTimeMillis() to expiration
        this[key] = value
    }

    override fun set(key: K, value: V) {
        this._map[key] = value
    }

    override fun get(key: K): V? {
        return if (!this.isKeyExpired(key)) {
            this._map[key]
        } else {
            this.remove(key)
            null
        }
    }

    override fun remove(key: K): V? {
        this._expirationMap.remove(key)
        return this._map.remove(key)
    }

    override fun containsKey(key: K): Boolean {
        return this._map.containsKey(key) && !this.isKeyExpired(key)
    }

    override fun clear() {
        this._map.clear()
        this._expirationMap.clear()
    }

    private fun isKeyExpired(key: K): Boolean {
        return if (this._expirationMap.containsKey(key)) {
            val (startTime, expiration) = this._expirationMap[key]!!
            System.currentTimeMillis() > startTime + expiration
        } else false
    }
}