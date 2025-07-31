package com.lovelycatv.vertex.json

import com.lovelycatv.vertex.json.exception.JsonParseException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JSONParserTest {
    private val parser = JSONParser()

    @Test
    fun initialize() {
        parser.initialize("{}")
    }

    @Test
    fun parseObject() {
        assertThrowsExactly(JsonParseException::class.java) {
            parser.initialize("{").parseObject()
        }

        assertThrowsExactly(JsonParseException::class.java) {
            parser.initialize("}").parseObject()
        }

        assertThrowsExactly(JsonParseException::class.java) {
            parser.initialize("!@#$%^&*()").parseObject()
        }

        val e1 = parser.initialize("{}").parseObject()
        val e2 = parser.initialize("  {}").parseObject()
        val e3 = parser.initialize("{  }").parseObject()
        val e4 = parser.initialize("  {   }").parseObject()

        assertTrue(e1.isEmpty())
        assertTrue(e2.isEmpty())
        assertTrue(e3.isEmpty())
        assertTrue(e4.isEmpty())

        val c1 = parser.initialize("{\"a\": 1}").parseObject().also { println(it) }
        val c2 = parser.initialize("{\"a\": 4e-3, \"b\": \"Vertex JSON\"}").parseObject().also { println(it) }
        val c3 = parser.initialize("{\"a\": -123.233, \"b\": [1, 2, 3, 4, {\"c\": [5, 6, 7]}]}").parseObject().also { println(it) }

        assertEquals(c1["a"], 1L)

        assertEquals(c2["a"], 4e-3)
        assertEquals(c2["b"], "Vertex JSON")

        assertEquals(c3["a"], -123.233)
        assertTrue(c3["b"] is List<*>)
        assertTrue((c3["b"] as List<*>).size == 5)
        assertTrue((c3["b"] as List<*>)[0] == 1L)
        assertTrue((c3["b"] as List<*>)[1] == 2L)
        assertTrue((c3["b"] as List<*>)[2] == 3L)
        assertTrue((c3["b"] as List<*>)[3] == 4L)
        assertTrue((c3["b"] as List<*>)[4] is Map<*, *>)
        assertTrue(((c3["b"] as List<*>)[4] as Map<*, *>)["c"] is List<*>)
        assertTrue((((c3["b"] as List<*>)[4] as Map<*, *>)["c"] as List<*>)[0] == 5L)
        assertTrue((((c3["b"] as List<*>)[4] as Map<*, *>)["c"] as List<*>)[1] == 6L)
        assertTrue((((c3["b"] as List<*>)[4] as Map<*, *>)["c"] as List<*>)[2] == 7L)
    }

    @Test
    fun parseArray() {
        assertThrowsExactly(JsonParseException::class.java) {
            parser.initialize("{}").parseArray()
        }
        assertThrowsExactly(JsonParseException::class.java) {
            parser.initialize("[").parseArray()
        }
        assertThrowsExactly(JsonParseException::class.java) {
            parser.initialize("]").parseArray()
        }

        val e1 = parser.initialize("[]").parseArray()
        val e2 = parser.initialize("  []").parseArray()
        val e3 = parser.initialize("[  ]").parseArray()
        val e4 = parser.initialize("  [  ]").parseArray()

        assertTrue(e1.isEmpty())
        assertTrue(e2.isEmpty())
        assertTrue(e3.isEmpty())
        assertTrue(e4.isEmpty())
    }
}