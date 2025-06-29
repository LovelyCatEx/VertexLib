package com.lovelycatv.vertex.cache

import com.lovelycatv.vertex.cache.key.FixedCacheKey
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CacheTemplateTest {
    private val dataSource = mapOf(
        "1" to "1",
        "2" to "4",
        "3" to "9"
    )

    private val inMemoryCacheTemplate = buildCacheTemplate<String, String> {
        inMemoryCacheSource()
        fixedCacheKey(0, "abc")
        mutableCacheKey(1, "def:?") {
            provide { keyFormat, args ->
                keyFormat.replace("?", args[0].toString())
            }
            provideForSetValue { keyFormat, data ->
                keyFormat.replace("?", data)
            }
        }
        dataSource { id, args ->
            when (id) {
                0 -> dataSource.toList().joinToString()
                1 -> dataSource[args[0].toString()]
                else -> "Unknown"
            }
        }
    }

    @BeforeEach
    fun beforeEach() {
        inMemoryCacheTemplate.clear()
    }

    @Test
    fun getCacheKeys() {
        assertTrue(inMemoryCacheTemplate.cacheKeys.size == 2)
    }

    @Test
    fun addCacheKey() {
        inMemoryCacheTemplate.addCacheKey(233, FixedCacheKey("ghi"))
        assertTrue(inMemoryCacheTemplate.cacheKeys.size == 3)
    }

    @Test
    fun get() {
        assertEquals(dataSource.toList().joinToString(), inMemoryCacheTemplate.get(0))
        assertEquals("1", inMemoryCacheTemplate.get(1, 1))
        assertEquals("4", inMemoryCacheTemplate.get(1, 2))
        assertEquals("9", inMemoryCacheTemplate.get(1, 3))
    }

    @Test
    fun set() {
        assertEquals(null, inMemoryCacheTemplate["abc"])
        assertEquals(null, inMemoryCacheTemplate["def:1"])

        inMemoryCacheTemplate.get(0)
        inMemoryCacheTemplate.get(1, 1)

        assertEquals(dataSource.toList().joinToString(), inMemoryCacheTemplate.get(0))
        assertEquals("1", inMemoryCacheTemplate.get(1, 1))
    }

    @Test
    fun testSetExpiration() {
        inMemoryCacheTemplate.set(0, "abc", 100)
        assertTrue(inMemoryCacheTemplate.keys.size == 1)
        assertEquals("abc", inMemoryCacheTemplate.get(0))

        Thread.sleep(120)
        assertTrue(inMemoryCacheTemplate.keys.isEmpty())
        assertEquals(dataSource.toList().joinToString(), inMemoryCacheTemplate.get(0))
    }
}