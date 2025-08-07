package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.VertexASMLog
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author lovelycat
 * @since 2025-08-07 15:19
 * @version 1.0
 */
class StaticInitMethodTest {
    @BeforeEach
    fun beforeEach() {
        VertexASMLog.setEnableDebugging(true)
    }

    @Test
    fun test() {
        val testClassName = "TestClass"
        val testClass = ClassDeclaration
    }
}