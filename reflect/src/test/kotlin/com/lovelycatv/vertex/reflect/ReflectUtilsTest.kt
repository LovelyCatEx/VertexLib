package com.lovelycatv.vertex.reflect

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class ReflectUtilsTest {

    @Test
    fun isPrimitiveType() {
        assertTrue(ReflectUtils.isPrimitiveType(String::class, true))
    }

    @Test
    fun isPrimitiveTypeForJava() {
        assertTrue(ReflectUtils.isPrimitiveType(String::class.java, true))
    }

    @Test
    fun copyObject() {
        val testClass = TestClass()
        val copiedTestClass = ReflectUtils.copyObject(testClass) { it }
        assertTrue(testClass != copiedTestClass)
    }

    @Test
    fun shallowCopy() {
        val testClass = TestClass()
        val copiedTestClass = ReflectUtils.shallowCopy(testClass)
        copiedTestClass.d.add(2025)
        assertTrue(testClass.d.size == 5 && testClass.d[4] == 2025)
    }

    @Test
    fun deepCopy() {
        val testClass = TestClass()
        val copiedTestClass = ReflectUtils.deepCopy(testClass)
        copiedTestClass.c[3] = 6
        copiedTestClass.d[3] = 6
        copiedTestClass.d.add(2025)
        assertTrue(testClass.d.size == 4 && testClass.c[3] == 5 && testClass.d[3] == 5)
    }
}