package com.lovelycatv.vertex.json

import com.lovelycatv.vertex.json.exception.JsonNodeAccessException
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class JSONObjectTest {
    private lateinit var jsonObject: JSONObject
    private lateinit var jsonObject2: JSONObject

    @BeforeEach
    fun beforeEach() {
        jsonObject = JSONObject.parse("{\"a\": \"Vertex JSONObject\", \"b\": 123, \"c\": -314159e-5, \"d\": {\"d1\": [1, 2, 3]}, \"e\": [4, 5, 6, {\"e1\": 1, \"e2\": -3e-1}, 7]}")
        jsonObject2 = JSONObject(mutableMapOf("a" to 1.toShort(), "b" to 2, "c" to 3L, "d" to 4f, "e" to 5.0))
    }

    @Test
    fun getJSONObject() {
        val obj = jsonObject.getJSONObject("d")
        assertTrue(obj != null)

        assertThrowsExactly(JsonNodeAccessException::class.java) {
            jsonObject.getJSONObject("b")
        }
    }

    @Test
    fun getJSONArray() {
        val obj = jsonObject.getJSONObject("d")
        assertTrue(obj is JSONObject)
        assertTrue(obj != null && obj.getJSONArray("d1") is JSONArray)
    }

    @Test
    fun getString() {
        assertEquals(jsonObject.getString("a"), "Vertex JSONObject")
    }

    @Test
    fun getShort() {
        assertEquals(jsonObject2.getShort("a"), 1.toShort())
    }

    @Test
    fun getInteger() {
        assertEquals(jsonObject2.getInteger("b"), 2)
    }

    @Test
    fun getLong() {
        assertEquals(jsonObject2.getLong("c"), 3L)
    }

    @Test
    fun getFloat() {
        assertEquals(jsonObject2.getFloat("d"), 4f)
    }

    @Test
    fun getDouble() {
        assertEquals(jsonObject2.getDouble("e"), 5.0)
    }

    @Test
    fun toJSONString() {
        val expected = "{\"a\": \"Vertex JSONObject\", \"b\": 123, \"c\": -3.14159, \"d\": {\"d1\": [1, 2, 3]}, \"e\": [4, 5, 6, {\"e1\": 1, \"e2\": -0.3}, 7]}"
        assertEquals(jsonObject.toJSONString(), expected)
        assertEquals(jsonObject.toJSONString(), JSONObject.parse(jsonObject.toJSONString()).toJSONString())
    }

    @Test
    fun get() {
        assertEquals(jsonObject["a"], "Vertex JSONObject")
    }

    @Test
    fun remove() {
        jsonObject.remove("a")
        assertTrue(jsonObject.getString("a") == null)
    }
}