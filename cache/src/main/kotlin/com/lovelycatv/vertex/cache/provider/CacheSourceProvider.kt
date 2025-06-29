package com.lovelycatv.vertex.cache.provider

import com.lovelycatv.vertex.cache.store.ExpiringKVStore

/**
 * @author lovelycat
 * @since 2025-06-29 09:54
 * @version 1.0
 */
interface CacheSourceProvider<K: CharSequence, V> : ExpiringKVStore<K, V>