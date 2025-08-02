package com.lovelycatv.vertex.asm

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.objectweb.asm.Opcodes

class ASMUtilsTest {

    @Test
    fun isPrimitiveType() {
        assertTrue(ASMUtils.isPrimitiveType(Void::class.java))
        assertTrue(ASMUtils.isPrimitiveType(Short::class.java))
        assertTrue(ASMUtils.isPrimitiveType(Int::class.java))
        assertTrue(ASMUtils.isPrimitiveType(Long::class.java))
        assertTrue(ASMUtils.isPrimitiveType(Float::class.java))
        assertTrue(ASMUtils.isPrimitiveType(Double::class.java))
        assertTrue(ASMUtils.isPrimitiveType(Boolean::class.java))
        assertTrue(ASMUtils.isPrimitiveType(Char::class.java))
        assertTrue(ASMUtils.isPrimitiveType(Byte::class.java))
    }

    @Test
    fun getDescriptor() {
        assertEquals("V", ASMUtils.getDescriptor(Void::class.java))
        assertEquals("S", ASMUtils.getDescriptor(Short::class.java))
        assertEquals("I", ASMUtils.getDescriptor(Int::class.java))
        assertEquals("J", ASMUtils.getDescriptor(Long::class.java))
        assertEquals("F", ASMUtils.getDescriptor(Float::class.java))
        assertEquals("D", ASMUtils.getDescriptor(Double::class.java))
        assertEquals("Z", ASMUtils.getDescriptor(Boolean::class.java))
        assertEquals("C", ASMUtils.getDescriptor(Char::class.java))
        assertEquals("B", ASMUtils.getDescriptor(Byte::class.java))
        assertEquals("Ljava/lang/String;", ASMUtils.getDescriptor(String::class.java))
        assertEquals("[Ljava/lang/Object;", ASMUtils.getDescriptor(Array::class.java))
    }

    @Test
    fun getArrayDescriptor() {
        assertEquals("[S", ASMUtils.getArrayDescriptor(Short::class.java, 1))
        assertEquals("[[I", ASMUtils.getArrayDescriptor(Int::class.java, 2))
        assertEquals("[[Ljava/lang/String;", ASMUtils.getArrayDescriptor(String::class.java, 2))
    }

    @Test
    fun countSlots() {
        assertEquals(1, ASMUtils.countSlots(Int::class.java))
        assertEquals(1, ASMUtils.countSlots(String::class.java))
        assertEquals(2, ASMUtils.countSlots(Double::class.java))
        assertEquals(2, ASMUtils.countSlots(Long::class.java))
    }

    @Test
    fun getLoadOpcode() {
        assertEquals(JVMInstruction.ILOAD, ASMUtils.getLoadOpcode(Boolean::class.java))
        assertEquals(JVMInstruction.ILOAD, ASMUtils.getLoadOpcode(Byte::class.java))
        assertEquals(JVMInstruction.ILOAD, ASMUtils.getLoadOpcode(Char::class.java))
        assertEquals(JVMInstruction.ILOAD, ASMUtils.getLoadOpcode(Short::class.java))
        assertEquals(JVMInstruction.ILOAD, ASMUtils.getLoadOpcode(Int::class.java))

        assertEquals(JVMInstruction.LLOAD, ASMUtils.getLoadOpcode(Long::class.java))
        assertEquals(JVMInstruction.FLOAD, ASMUtils.getLoadOpcode(Float::class.java))
        assertEquals(JVMInstruction.DLOAD, ASMUtils.getLoadOpcode(Double::class.java))

        assertEquals(JVMInstruction.ALOAD, ASMUtils.getLoadOpcode(String::class.java))
    }

    @Test
    fun getStoreOpcode() {
        assertEquals(JVMInstruction.ISTORE, ASMUtils.getStoreOpcode(Boolean::class.java))
        assertEquals(JVMInstruction.ISTORE, ASMUtils.getStoreOpcode(Byte::class.java))
        assertEquals(JVMInstruction.ISTORE, ASMUtils.getStoreOpcode(Char::class.java))
        assertEquals(JVMInstruction.ISTORE, ASMUtils.getStoreOpcode(Short::class.java))
        assertEquals(JVMInstruction.ISTORE, ASMUtils.getStoreOpcode(Int::class.java))

        assertEquals(JVMInstruction.LSTORE, ASMUtils.getStoreOpcode(Long::class.java))
        assertEquals(JVMInstruction.FSTORE, ASMUtils.getStoreOpcode(Float::class.java))
        assertEquals(JVMInstruction.DSTORE, ASMUtils.getStoreOpcode(Double::class.java))

        assertEquals(JVMInstruction.ASTORE, ASMUtils.getStoreOpcode(String::class.java))
    }

    @Test
    fun getReturnOpcode() {
        assertEquals(JVMInstruction.IRETURN, ASMUtils.getReturnOpcode(Boolean::class.java))
        assertEquals(JVMInstruction.IRETURN, ASMUtils.getReturnOpcode(Byte::class.java))
        assertEquals(JVMInstruction.IRETURN, ASMUtils.getReturnOpcode(Char::class.java))
        assertEquals(JVMInstruction.IRETURN, ASMUtils.getReturnOpcode(Short::class.java))
        assertEquals(JVMInstruction.IRETURN, ASMUtils.getReturnOpcode(Int::class.java))

        assertEquals(JVMInstruction.LRETURN, ASMUtils.getReturnOpcode(Long::class.java))
        assertEquals(JVMInstruction.FRETURN, ASMUtils.getReturnOpcode(Float::class.java))
        assertEquals(JVMInstruction.DRETURN, ASMUtils.getReturnOpcode(Double::class.java))

        assertEquals(JVMInstruction.ARETURN, ASMUtils.getReturnOpcode(String::class.java))
        assertEquals(JVMInstruction.RETURN, ASMUtils.getReturnOpcode(Void::class.java))
    }

    @Test
    fun getInternalName() {
        assertEquals("java/lang/Object", ASMUtils.getInternalName(java.lang.Object::class.java))
        assertEquals("java/lang/String", ASMUtils.getInternalName(String::class.java))
        assertEquals("int", ASMUtils.getInternalName(Int::class.java))
        assertEquals("char", ASMUtils.getInternalName(Char::class.java))
        assertEquals("byte", ASMUtils.getInternalName(Byte::class.java))
        assertEquals("short", ASMUtils.getInternalName(Short::class.java))
    }

    @Test
    fun toAccessCode() {
        assertEquals(Opcodes.ACC_PUBLIC, ASMUtils.toAccessCode(JavaModifier.PUBLIC))
        assertEquals(Opcodes.ACC_PRIVATE, ASMUtils.toAccessCode(JavaModifier.PRIVATE))
        assertEquals(Opcodes.ACC_PROTECTED, ASMUtils.toAccessCode(JavaModifier.PROTECTED))
        assertEquals(Opcodes.ACC_FINAL, ASMUtils.toAccessCode(JavaModifier.FINAL))
        assertEquals(Opcodes.ACC_ABSTRACT, ASMUtils.toAccessCode(JavaModifier.ABSTRACT))
        assertEquals(Opcodes.ACC_STATIC, ASMUtils.toAccessCode(JavaModifier.STATIC))
        assertEquals(Opcodes.ACC_VOLATILE, ASMUtils.toAccessCode(JavaModifier.VOLATILE))
        assertEquals(Opcodes.ACC_SYNCHRONIZED, ASMUtils.toAccessCode(JavaModifier.SYNCHRONIZED))
        assertEquals(Opcodes.ACC_TRANSIENT, ASMUtils.toAccessCode(JavaModifier.TRANSIENT))
        assertEquals(Opcodes.ACC_NATIVE, ASMUtils.toAccessCode(JavaModifier.NATIVE))
        assertEquals(Opcodes.ACC_STRICT, ASMUtils.toAccessCode(JavaModifier.STRICTFP))
        assertEquals(Opcodes.ACC_ENUM, ASMUtils.toAccessCode(JavaKeyWord.ENUM))
        assertEquals(Opcodes.ACC_RECORD, ASMUtils.toAccessCode(JavaKeyWord.RECORD))
        assertEquals(Opcodes.ACC_INTERFACE, ASMUtils.toAccessCode(JavaKeyWord.INTERFACE))
        assertEquals(Opcodes.ACC_SUPER, ASMUtils.toAccessCode(JavaKeyWord.SUPER))
    }

    @Test
    fun constValues() {
        assertEquals(ASMUtils.OBJECT_CLASS, java.lang.Object::class.java)
        assertEquals(ASMUtils.OBJECT_INTERNAL_NAME, ASMUtils.getInternalName(ASMUtils.OBJECT_CLASS))
        assertEquals(ASMUtils.OBJECT_DESCRIPTOR, ASMUtils.getDescriptor(ASMUtils.OBJECT_CLASS))

    }
}