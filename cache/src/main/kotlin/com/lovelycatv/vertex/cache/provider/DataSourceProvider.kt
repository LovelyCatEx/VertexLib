package com.lovelycatv.vertex.cache.provider

/**
 * @author lovelycat
 * @since 2025-06-29 09:47
 * @version 1.0
 */
fun interface DataSourceProvider<K: CharSequence, V> {
    fun provide(id: Int, vararg args: Any?): V?
}