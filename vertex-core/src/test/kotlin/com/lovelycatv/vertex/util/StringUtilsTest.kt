package com.lovelycatv.vertex.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringUtilsTest {
    @Test
    fun orderReplace() {
        val text = "Hello, \$T!"
        val text2 = "Hello, \$T! 1 + 1 = \$D"

        assertEquals("Hello, Vertex Core!", StringUtils.orderReplace(text, arrayOf("\$T"), "Vertex Core"))
        assertEquals("Hello, Vertex Core! 1 + 1 = 2", StringUtils.orderReplace(text2, arrayOf("\$T", "\$D"), "Vertex Core", "2"))
    }
}