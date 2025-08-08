package com.lovelycatv.vertex.reflect.enhanced

import com.lovelycatv.vertex.log.logger
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnhancedClassTest {
    private val logger = logger()
    private lateinit var enhancedLargeClass: EnhancedClass

    @BeforeAll
    fun beforeAll() {
        logger.debug("Creating EnhancedLargeClass...")
        val s = System.nanoTime()
        EnhancedClass.precache(LargeClass::class.java)
        logger.debug("EnhancedLargeClass created, costs: ${1.0 * (System.nanoTime() - s) / 1000000}ms")

        logger.debug("Testing EnhancedClass cache...")
        val s2 = System.nanoTime()
        enhancedLargeClass = EnhancedClass.create(LargeClass::class.java)
        logger.debug("Get EnhancedLargeClass from cache, costs: ${1.0 * (System.nanoTime() - s2) / 1000000}ms")
    }

    @Test
    fun invokeMethod() {
        val instance = LargeClass()

        val r1 = enhancedLargeClass.invokeMethod(
            instance,
            enhancedLargeClass.getIndex("method1", Int::class.java, Short::class.java, Float::class.java),
            1, 1.toShort(), 2f
        )
        assertEquals(4.0, r1)

        val r2 = enhancedLargeClass.invokeMethod(
            instance,
            enhancedLargeClass.getIndex("method1", Int::class.java, Byte::class.java, Float::class.java),
            1, 3.toByte(), 2f
        )
        assertEquals("132.0", r2)
    }
}