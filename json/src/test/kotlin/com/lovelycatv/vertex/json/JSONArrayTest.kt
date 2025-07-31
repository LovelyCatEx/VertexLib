package com.lovelycatv.vertex.json

import com.lovelycatv.vertex.json.exception.JsonNodeAccessException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach

class JSONArrayTest {
    private lateinit var numberJsonArray: JSONArray
    private lateinit var numberJsonArray2: JSONArray
    private lateinit var stringJsonArray: JSONArray
    private lateinit var flexJsonArray: JSONArray

    @BeforeEach
    fun beforeEach() {
        numberJsonArray = JSONArray.parse("[1, 2, 3, 4, 5]")
        numberJsonArray2 = JSONArray(listOf(1.toShort(), 2, 3L, 4f, 5.0))
        stringJsonArray = JSONArray.parse("[\"1\", \"2\", \"hello, world!\"]")
        flexJsonArray = JSONArray.parse("[1, \"2\", {\"a\": 3}, [4, 5, 6]]")
    }

    @Test
    fun getJSONObject() {
        assertTrue(flexJsonArray.getJSONObject(2) != null)
        assertThrowsExactly(JsonNodeAccessException::class.java) {
            flexJsonArray.getJSONArray(2)
            flexJsonArray.getString(2)
        }
    }

    @Test
    fun getJSONArray() {
        assertTrue(flexJsonArray.getJSONArray(3) != null)
        assertThrowsExactly(JsonNodeAccessException::class.java) {
            flexJsonArray.getJSONObject(3)
            flexJsonArray.getString(3)
        }
    }

    @Test
    fun getString() {
        assertEquals(stringJsonArray.getString(0), "1")
        assertEquals(stringJsonArray.getString(1), "2")
        assertEquals(flexJsonArray.getString(1), "2")
        assertThrowsExactly(JsonNodeAccessException::class.java) {
            numberJsonArray.getString(0)
        }
    }

    @Test
    fun getShort() {
        assertEquals(numberJsonArray2.getShort(0), 1.toShort())
    }

    @Test
    fun getInteger() {
        assertEquals(numberJsonArray2.getInteger(1), 2)
    }

    @Test
    fun getLong() {
        assertEquals(numberJsonArray2.getLong(2), 3L)
    }

    @Test
    fun getFloat() {
        assertEquals(numberJsonArray2.getFloat(3), 4f)
    }

    @Test
    fun getDouble() {
        assertEquals(numberJsonArray2.getDouble(4), 5.0)
    }

    @Test
    fun toJSONString() {
        assertEquals(flexJsonArray.toJSONString(), "[1, \"2\", {\"a\": 3}, [4, 5, 6]]")
    }
}