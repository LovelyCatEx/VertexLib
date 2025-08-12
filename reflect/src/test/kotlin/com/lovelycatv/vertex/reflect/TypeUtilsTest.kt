package com.lovelycatv.vertex.reflect

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TypeUtilsTest {
    @Test
    fun getArrayClass() {
        val r1 = TypeUtils.getArrayClass(String::class.java, 1)
        val r2 = TypeUtils.getArrayClass(String::class.java, 2)
        val r3 = TypeUtils.getArrayClass(r2, 2)
        val r4 = TypeUtils.getArrayClass(Int::class.java, 3)
        val r5 = TypeUtils.getArrayClass(BaseDataType.PACKAGED_BOOLEAN_CLASS, 4)

        kotlin.test.assertEquals(String::class.java, TypeUtils.getArrayComponent(r1))
        kotlin.test.assertEquals(1, TypeUtils.getArrayDimensions(r1))

        kotlin.test.assertEquals(String::class.java, TypeUtils.getArrayComponent(r2))
        kotlin.test.assertEquals(2, TypeUtils.getArrayDimensions(r2))

        kotlin.test.assertEquals(String::class.java, TypeUtils.getArrayComponent(r3))
        kotlin.test.assertEquals(4, TypeUtils.getArrayDimensions(r3))

        kotlin.test.assertEquals(Int::class.java, TypeUtils.getArrayComponent(r4))
        kotlin.test.assertEquals(3, TypeUtils.getArrayDimensions(r4))

        kotlin.test.assertEquals(BaseDataType.PACKAGED_BOOLEAN_CLASS, TypeUtils.getArrayComponent(r5))
        kotlin.test.assertEquals(4, TypeUtils.getArrayDimensions(r5))
    }

    @Test
    fun isPrimitiveTypeForPackaged() {
        assertFalse(TypeUtils.isPrimitiveType(String::class.java, true))
        assertTrue(TypeUtils.isPrimitiveType(Int::class.java, true))
    }

    @Test
    fun isPrimitiveType() {
        assertFalse(TypeUtils.isPrimitiveType(Void.TYPE))
        assertFalse(TypeUtils.isPrimitiveType(Void::class.java))
        assertTrue(TypeUtils.isPrimitiveType(Short::class.java))
        assertTrue(TypeUtils.isPrimitiveType(Int::class.java))
        assertTrue(TypeUtils.isPrimitiveType(Long::class.java))
        assertTrue(TypeUtils.isPrimitiveType(Float::class.java))
        assertTrue(TypeUtils.isPrimitiveType(Double::class.java))
        assertTrue(TypeUtils.isPrimitiveType(Boolean::class.java))
        assertTrue(TypeUtils.isPrimitiveType(Char::class.java))
        assertTrue(TypeUtils.isPrimitiveType(Byte::class.java))
        assertFalse(TypeUtils.isPrimitiveType(String::class.java))
    }

    @Test
    fun getDescriptor() {
        assertEquals("V", TypeUtils.getDescriptor(Void::class.java))
        assertEquals("S", TypeUtils.getDescriptor(Short::class.java))
        assertEquals("I", TypeUtils.getDescriptor(Int::class.java))
        assertEquals("J", TypeUtils.getDescriptor(Long::class.java))
        assertEquals("F", TypeUtils.getDescriptor(Float::class.java))
        assertEquals("D", TypeUtils.getDescriptor(Double::class.java))
        assertEquals("Z", TypeUtils.getDescriptor(Boolean::class.java))
        assertEquals("C", TypeUtils.getDescriptor(Char::class.java))
        assertEquals("B", TypeUtils.getDescriptor(Byte::class.java))
        assertEquals("Ljava/lang/String;", TypeUtils.getDescriptor(String::class.java))
        assertEquals("[Ljava/lang/Object;", TypeUtils.getDescriptor(Array::class.java))
    }

    @Test
    fun getArrayDescriptor() {
        assertEquals("[S", TypeUtils.getArrayDescriptor(Short::class.java, 1))
        assertEquals("[[I", TypeUtils.getArrayDescriptor(Int::class.java, 2))
        assertEquals("[[Ljava/lang/String;", TypeUtils.getArrayDescriptor(String::class.java, 2))
    }

    @Test
    fun getInternalName() {
        assertEquals("java/lang/Object", TypeUtils.getInternalName(java.lang.Object::class.java))
        assertEquals("java/lang/String", TypeUtils.getInternalName(String::class.java))
        assertThrowsExactly(IllegalArgumentException::class.java) {
            assertEquals("int", TypeUtils.getInternalName(Int::class.java))
        }
        assertThrowsExactly(IllegalArgumentException::class.java) {
            assertEquals("char", TypeUtils.getInternalName(Char::class.java))
        }
        assertThrowsExactly(IllegalArgumentException::class.java) {
            assertEquals("byte", TypeUtils.getInternalName(Byte::class.java))
        }
        assertThrowsExactly(IllegalArgumentException::class.java) {
            assertEquals("short", TypeUtils.getInternalName(Short::class.java))
        }
    }

}