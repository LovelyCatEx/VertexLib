package com.lovelycatv.vertex.cache

import com.lovelycatv.vertex.cache.key.CacheKey
import com.lovelycatv.vertex.cache.key.FixedCacheKey
import com.lovelycatv.vertex.cache.key.MutableCacheKey
import com.lovelycatv.vertex.cache.provider.CacheSourceProvider
import com.lovelycatv.vertex.cache.provider.DataSourceProvider
import com.lovelycatv.vertex.cache.provider.InMemoryCacheSourceProvider

/**
 * @author lovelycat
 * @since 2025-06-29 09:51
 * @version 1.0
 */
class CacheTemplateBuilder<K: CharSequence, V> {
    private var cacheSourceProvider: CacheSourceProvider<K, V>? = null
    private var dataSourceProvider: DataSourceProvider<K, V>? = null
    private val cacheKeys: MutableMap<Int, CacheKey<K, V>> = mutableMapOf()

    fun cacheSource(provider: CacheSourceProvider<K, V>): CacheTemplateBuilder<K, V> {
        this.cacheSourceProvider = provider
        return this
    }

    fun inMemoryCacheSource(): CacheTemplateBuilder<K, V> {
        this.cacheSourceProvider = InMemoryCacheSourceProvider()
        return this
    }

    fun dataSource(provider: DataSourceProvider<K, V>): CacheTemplateBuilder<K, V> {
        this.dataSourceProvider = provider
        return this
    }

    fun dataSource(provider: (id: Int, args: Array<out Any?>) -> V?): CacheTemplateBuilder<K, V> {
        this.dataSourceProvider = DataSourceProvider { id, a ->
            provider.invoke(id, a)
        }
        return this
    }

    fun cacheKey(id: Int, cacheKey: CacheKey<K, V>): CacheTemplateBuilder<K, V> {
        cacheKeys[id] = cacheKey
        return this
    }

    fun fixedCacheKey(id: Int, key: K): CacheTemplateBuilder<K, V> {
        this.cacheKey(id, FixedCacheKey(key))
        return this
    }

    fun mutableCacheKey(id: Int, keyFormat: K, keyBuilder: MutableCacheKey.Builder<K, V>.() -> Unit): CacheTemplateBuilder<K, V> {
        val builder = MutableCacheKey.Builder<K, V>(keyFormat)
        keyBuilder.invoke(builder)
        this.cacheKey(id, builder.build())
        return this
    }


    fun build(): CacheTemplate<K, V> {
        return if (this.cacheSourceProvider == null) {
            throw IllegalStateException("Please use cacheSource() to specify a cacheSource")
        } else if (this.dataSourceProvider == null) {
            throw IllegalStateException("Please use dataSource() to specify a cacheSource")
        } else {
            val template = object : CacheTemplate<K, V>(this.cacheSourceProvider!!, this.dataSourceProvider!!) {}
            template.addCacheKeys(this.cacheKeys)
            template
        }
    }
}

fun <K: CharSequence, V> buildCacheTemplate(
    fx: CacheTemplateBuilder<K, V>.() -> Unit
): CacheTemplate<K, V> {
    val builder = CacheTemplateBuilder<K, V>()
    fx.invoke(builder)
    return builder.build()
}