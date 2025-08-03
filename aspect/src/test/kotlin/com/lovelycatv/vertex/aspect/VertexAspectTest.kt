package com.lovelycatv.vertex.aspect

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class VertexAspectTest {

    @Test
    fun createJavaProxy() {
        val target = TargetClass()
        val proxy: ITargetClass = VertexAspect.createJavaProxy(target, object : SimpleAspect() {})
        proxy.sayHello("Vertex Aspect")
        val r1 = proxy.add(1, 2)
        val r2 = proxy.getIntArrayLength(IntArray(5) { it })
        val r3 = proxy.complexFunction(
            "Vertex Aspect",
            IntArray(5) { it },
            1f,
            arrayOf(DoubleArray(5) { it.toDouble() }),
            arrayOf(arrayOf(""))
        )

        assertEquals(3, r1)
        assertEquals(5, r2)
        assertEquals(21, r3)
    }

    @Test
    fun createProxy() {
        val target = TargetClass()
        val proxy = VertexAspect.createProxy(target, object : SimpleAspect() {})
        proxy.sayHello("Vertex Aspect")
        val r1 = proxy.add(1, 2)
        val r2 = proxy.getIntArrayLength(IntArray(5) { it })
        val r3 = proxy.complexFunction(
            "Vertex Aspect",
            IntArray(5) { it },
            1f,
            arrayOf(DoubleArray(5) { it.toDouble() }),
            arrayOf(arrayOf(""))
        )

        assertEquals(3, r1)
        assertEquals(5, r2)
        assertEquals(21, r3)
    }
}