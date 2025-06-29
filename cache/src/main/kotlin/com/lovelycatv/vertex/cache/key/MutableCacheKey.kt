package com.lovelycatv.vertex.cache.key

/**
 * @author lovelycat
 * @since 2025-06-28 19:06
 * @version 1.0
 */
class MutableCacheKey<K: CharSequence, V>(
    private val keyFormat: K,
    private val keyProvider: Provider<K, V>
) : CacheKey<K, V> {
    override fun getKey(vararg args: Any?): K {
        return keyProvider.provide(this.keyFormat, *args)
    }

    override fun getKeyForSetValue(data: V): K {
        return keyProvider.provideForSetValue(this.keyFormat, data)
    }

    interface Provider<K: CharSequence, V> {
        fun provide(keyFormat: K, vararg args: Any?): K

        fun provideForSetValue(keyFormat: K, data: V): K
    }

    class Builder<K: CharSequence, V>(private val keyFormat: K) {
        private var fxProvide: ((keyFormat: K, args: Array<out Any?>) -> K)? = null
        private var fxProvideForSetValue: ((keyFormat: K, data: V) -> K)? = null

        fun provide(provide: (keyFormat: K, args: Array<out Any?>) -> K): Builder<K, V> {
            this.fxProvide = provide
            return this
        }

        fun provideForSetValue(provideForSetValue: (keyFormat: K, data: V) -> K): Builder<K, V> {
            this.fxProvideForSetValue = provideForSetValue
            return this
        }

        fun build(): MutableCacheKey<K, V> {
            return if (this.fxProvide == null) {
                throw IllegalStateException("Please use provide() to set a keyProvider for getValue")
            } else if (this.fxProvideForSetValue == null) {
                throw IllegalStateException("Please use provideForSetValue() to set a keyProvider for setValue")
            } else {
                MutableCacheKey(
                    keyFormat = keyFormat,
                    keyProvider = object : Provider<K, V> {
                        override fun provide(keyFormat: K, vararg args: Any?): K {
                            return this@Builder.fxProvide!!.invoke(keyFormat, args)
                        }

                        override fun provideForSetValue(keyFormat: K, data: V): K {
                            return this@Builder.fxProvideForSetValue!!.invoke(keyFormat, data)
                        }
                    })
            }
        }
    }
}