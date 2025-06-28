package com.lovelycatv.vertex

import com.lovelycatv.vertex.cache.store.InMemoryKVStore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test

class InMemoryKVStoreTest {
    private val kvStore = InMemoryKVStore<String, String>()

    @BeforeEach
    fun beforeEach() {
        kvStore.clear()
    }

    @Test
    fun getSize() {
        assertTrue(kvStore.size == 0)
        kvStore["a"] = "b"
        assertTrue(kvStore.size == 1)
    }

    @Test
    fun getKeys() {
        assertTrue(kvStore.keys.isEmpty())
        kvStore["a"] = "b"
        assertTrue(kvStore.keys.size == 1)
        assertTrue(kvStore.keys.contains("a"))
    }

    @Test
    fun get() {
        kvStore["a"] = "b"
        assertTrue(kvStore["a"] == "b")
        assertTrue(kvStore["A"] == null)
    }

    @Test
    fun set() {
        kvStore["a"] = "b"
        assertTrue(kvStore.size == 1)
        assertTrue(kvStore["a"] == "b")
    }

    @Test
    fun setExpiration() {
        kvStore.set("a", "b", 100)
        assertTrue(kvStore.size == 1)
        assertTrue(kvStore.containsKey("a"))
        Thread.sleep(150)
        assertTrue(kvStore.size == 0)
        assertFalse(kvStore.containsKey("a"))

        kvStore.set("c", "d", 100)
        assertTrue(kvStore.size == 1)
        assertTrue(kvStore.containsKey("c"))
        Thread.sleep(50)
        assertTrue(kvStore.size == 1)
        assertTrue(kvStore.containsKey("c"))
        Thread.sleep(80)
        assertTrue(kvStore.size == 0)
        assertFalse(kvStore.containsKey("c"))
    }

    @Test
    fun remove() {
        kvStore["a"] = "b"
        assertTrue(kvStore["a"] == "b")
        kvStore.remove("a")
        assertTrue(kvStore["a"] == null)
    }
}