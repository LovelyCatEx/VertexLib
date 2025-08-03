package com.lovelycatv.vertex.reflect

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReflectUtilsTest {
    @Test
    fun invoke() {
        val t = TestClass()
        assertTrue(ReflectUtils.invoke(t, "getA") == t.a)
    }

    @Test
    fun isPrimitiveType() {
        assertFalse(ReflectUtils.isPrimitiveType(String::class, true))
        assertTrue(ReflectUtils.isPrimitiveType(Int::class, true))
    }

    @Test
    fun isPrimitiveTypeForJava() {
        assertFalse(ReflectUtils.isPrimitiveType(String::class.java, true))
        assertTrue(ReflectUtils.isPrimitiveType(Int::class.java, true))
    }

    @Test
    fun getAllDeclaredFields() {
        val testClass2Fields = ReflectUtils.getAllDeclaredFields(TestClass2())
        assertTrue(testClass2Fields.find { it.name == "f" } != null)
        assertTrue(testClass2Fields.map { it.name }.containsAll(ReflectUtils.getAllDeclaredFields(TestClass()).map { it.name }))
    }

    @Test
    fun getAllDeclaredProperties() {
        val testClass2Fields = ReflectUtils.getAllDeclaredProperties(TestClass2())
        assertTrue(testClass2Fields.find { it.name == "f" } != null)
        assertTrue(testClass2Fields.map { it.name }.containsAll(ReflectUtils.getAllDeclaredProperties(TestClass()).map { it.name }))
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

    @Test
    fun storeObjectFields() {
        val testClass = TestClass()
        val map = ReflectUtils.storeObjectFields(testClass, true).filterKeys { it != "\$jacocoData" }
        assertTrue(map.size == 5)
        assertTrue(map.keys.containsAll(listOf("a", "b", "c", "d", "e")))
        assertTrue(map["a"] == testClass.a)
        assertTrue(map["b"] == testClass.b)
        assertTrue(map["c"] == testClass.c)
        assertTrue(map["d"] == testClass.d)
        assertTrue(map["e"] == testClass.e)
    }

    @Test
    fun restoreObjectFields() {
        val testClass = TestClass()
        val map = ReflectUtils.storeObjectFields(testClass, true)

        val testClass2 = TestClass2()
        testClass2.a = "vertex#test"
        testClass2.b = 2026
        testClass2.c = arrayOf(2, 0, 2, 6)
        testClass2.d = mutableListOf(2, 0, 2, 6)
        testClass2.e = testClass
        testClass2.f = "TestClass2#test"

        ReflectUtils.restoreObjectFields(testClass2, map, false)
        assertTrue(testClass2.a != testClass.a)
        assertTrue(testClass2.f == "TestClass2#test")

        ReflectUtils.restoreObjectFields(testClass2, map, true)
        assertTrue(testClass2.a == testClass.a)
        assertTrue(testClass2.b == testClass.b)
        assertTrue(testClass2.c.contentEquals(testClass.c))
        assertTrue(testClass2.d == testClass.d)
        assertTrue(testClass2.e == testClass.e)
        assertTrue(testClass2.f == "TestClass2#test")
    }

    @Test
    fun getArrayDimensions() {
        class ArrayContainer {
            private val a: IntArray = IntArray(0)
            private val b: Array<IntArray> = arrayOf()
        }
        val array1Type = ArrayContainer::class.java.declaredFields.find { it.name == "a" }!!.type
        val array2Type = ArrayContainer::class.java.declaredFields.find { it.name == "b" }!!.type

        assertTrue(array1Type.isArray)
        assertTrue(array2Type.isArray)

        assertEquals(1, ReflectUtils.getArrayDimensions(array1Type))
        assertEquals(2, ReflectUtils.getArrayDimensions(array2Type))
    }

    @Test
    fun getArrayComponent() {
        class ArrayContainer {
            private val a: IntArray = IntArray(0)
            private val b: Array<IntArray> = arrayOf()
        }
        val array1Type = ArrayContainer::class.java.declaredFields.find { it.name == "a" }!!.type
        val array2Type = ArrayContainer::class.java.declaredFields.find { it.name == "b" }!!.type

        assertTrue(array1Type.isArray)
        assertTrue(array2Type.isArray)

        assertEquals(BaseDataType.INTEGER, ReflectUtils.getArrayComponent(array1Type).canonicalName)
        assertEquals(BaseDataType.INTEGER, ReflectUtils.getArrayComponent(array2Type).canonicalName)
    }
}