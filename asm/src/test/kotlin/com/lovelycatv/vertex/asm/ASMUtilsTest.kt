package com.lovelycatv.vertex.asm

import com.lovelycatv.vertex.asm.lang.code.calculate.CalculateType
import com.lovelycatv.vertex.reflect.TypeUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test
import org.objectweb.asm.Opcodes

class ASMUtilsTest {
    @Test
    fun countSlots() {
        assertEquals(1, ASMUtils.countSlots(Int::class.java))
        assertEquals(1, ASMUtils.countSlots(String::class.java))
        assertEquals(2, ASMUtils.countSlots(Double::class.java))
        assertEquals(2, ASMUtils.countSlots(Long::class.java))
    }

    @Test
    fun getLoadInstruction() {
        assertEquals(JVMInstruction.ILOAD, ASMUtils.getLoadInstruction(Boolean::class.java))
        assertEquals(JVMInstruction.ILOAD, ASMUtils.getLoadInstruction(Byte::class.java))
        assertEquals(JVMInstruction.ILOAD, ASMUtils.getLoadInstruction(Char::class.java))
        assertEquals(JVMInstruction.ILOAD, ASMUtils.getLoadInstruction(Short::class.java))
        assertEquals(JVMInstruction.ILOAD, ASMUtils.getLoadInstruction(Int::class.java))

        assertEquals(JVMInstruction.LLOAD, ASMUtils.getLoadInstruction(Long::class.java))
        assertEquals(JVMInstruction.FLOAD, ASMUtils.getLoadInstruction(Float::class.java))
        assertEquals(JVMInstruction.DLOAD, ASMUtils.getLoadInstruction(Double::class.java))

        assertEquals(JVMInstruction.ALOAD, ASMUtils.getLoadInstruction(String::class.java))
    }

    @Test
    fun getStoreInstruction() {
        assertEquals(JVMInstruction.ISTORE, ASMUtils.getStoreInstruction(Boolean::class.java))
        assertEquals(JVMInstruction.ISTORE, ASMUtils.getStoreInstruction(Byte::class.java))
        assertEquals(JVMInstruction.ISTORE, ASMUtils.getStoreInstruction(Char::class.java))
        assertEquals(JVMInstruction.ISTORE, ASMUtils.getStoreInstruction(Short::class.java))
        assertEquals(JVMInstruction.ISTORE, ASMUtils.getStoreInstruction(Int::class.java))

        assertEquals(JVMInstruction.LSTORE, ASMUtils.getStoreInstruction(Long::class.java))
        assertEquals(JVMInstruction.FSTORE, ASMUtils.getStoreInstruction(Float::class.java))
        assertEquals(JVMInstruction.DSTORE, ASMUtils.getStoreInstruction(Double::class.java))

        assertEquals(JVMInstruction.ASTORE, ASMUtils.getStoreInstruction(String::class.java))
    }

    @Test
    fun getReturnInstruction() {
        assertEquals(JVMInstruction.IRETURN, ASMUtils.getReturnInstruction(Boolean::class.java))
        assertEquals(JVMInstruction.IRETURN, ASMUtils.getReturnInstruction(Byte::class.java))
        assertEquals(JVMInstruction.IRETURN, ASMUtils.getReturnInstruction(Char::class.java))
        assertEquals(JVMInstruction.IRETURN, ASMUtils.getReturnInstruction(Short::class.java))
        assertEquals(JVMInstruction.IRETURN, ASMUtils.getReturnInstruction(Int::class.java))

        assertEquals(JVMInstruction.LRETURN, ASMUtils.getReturnInstruction(Long::class.java))
        assertEquals(JVMInstruction.FRETURN, ASMUtils.getReturnInstruction(Float::class.java))
        assertEquals(JVMInstruction.DRETURN, ASMUtils.getReturnInstruction(Double::class.java))

        assertEquals(JVMInstruction.ARETURN, ASMUtils.getReturnInstruction(String::class.java))
        assertEquals(JVMInstruction.RETURN, ASMUtils.getReturnInstruction(Void::class.java))
        assertEquals(JVMInstruction.RETURN, ASMUtils.getReturnInstruction(Void.TYPE))
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
        assertEquals(ASMUtils.OBJECT_INTERNAL_NAME, TypeUtils.getInternalName(ASMUtils.OBJECT_CLASS))
        assertEquals(ASMUtils.OBJECT_DESCRIPTOR, TypeUtils.getDescriptor(ASMUtils.OBJECT_CLASS))
    }

    @Test
    fun getLoadConstInstruction() {
        // Int
        assertEquals(JVMInstruction.ICONST_M1, ASMUtils.getLoadConstInstruction(-1))
        assertEquals(JVMInstruction.ICONST_0, ASMUtils.getLoadConstInstruction(0))
        assertEquals(JVMInstruction.ICONST_1, ASMUtils.getLoadConstInstruction(1))
        assertEquals(JVMInstruction.ICONST_2, ASMUtils.getLoadConstInstruction(2))
        assertEquals(JVMInstruction.ICONST_3, ASMUtils.getLoadConstInstruction(3))
        assertEquals(JVMInstruction.ICONST_4, ASMUtils.getLoadConstInstruction(4))
        assertEquals(JVMInstruction.ICONST_5, ASMUtils.getLoadConstInstruction(5))
        assertEquals(JVMInstruction.BIPUSH, ASMUtils.getLoadConstInstruction(6))
        assertEquals(JVMInstruction.BIPUSH, ASMUtils.getLoadConstInstruction(7))
        assertEquals(JVMInstruction.BIPUSH, ASMUtils.getLoadConstInstruction(8))

        assertEquals(JVMInstruction.BIPUSH, ASMUtils.getLoadConstInstruction(-110))
        assertEquals(JVMInstruction.SIPUSH, ASMUtils.getLoadConstInstruction(-30000))
        assertEquals(JVMInstruction.BIPUSH, ASMUtils.getLoadConstInstruction(100))
        assertEquals(JVMInstruction.SIPUSH, ASMUtils.getLoadConstInstruction(32760))

        // Byte
        assertEquals(JVMInstruction.ICONST_M1, ASMUtils.getLoadConstInstruction((-1).toByte()))
        assertEquals(JVMInstruction.ICONST_0, ASMUtils.getLoadConstInstruction(0.toByte()))
        assertEquals(JVMInstruction.ICONST_1, ASMUtils.getLoadConstInstruction(1.toByte()))
        assertEquals(JVMInstruction.ICONST_2, ASMUtils.getLoadConstInstruction(2.toByte()))
        assertEquals(JVMInstruction.ICONST_3, ASMUtils.getLoadConstInstruction(3.toByte()))
        assertEquals(JVMInstruction.ICONST_4, ASMUtils.getLoadConstInstruction(4.toByte()))
        assertEquals(JVMInstruction.ICONST_5, ASMUtils.getLoadConstInstruction(5.toByte()))
        assertEquals(JVMInstruction.BIPUSH, ASMUtils.getLoadConstInstruction(6.toByte()))

        // Char
        assertEquals(JVMInstruction.BIPUSH, ASMUtils.getLoadConstInstruction('a'))
        assertEquals(JVMInstruction.BIPUSH, ASMUtils.getLoadConstInstruction('b'))
        assertEquals(JVMInstruction.BIPUSH, ASMUtils.getLoadConstInstruction('c'))

        // Boolean
        assertEquals(JVMInstruction.ICONST_1, ASMUtils.getLoadConstInstruction(true))
        assertEquals(JVMInstruction.ICONST_0, ASMUtils.getLoadConstInstruction(false))

        // Long
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(-1L))
        assertEquals(JVMInstruction.LCONST_0, ASMUtils.getLoadConstInstruction(0L))
        assertEquals(JVMInstruction.LCONST_1, ASMUtils.getLoadConstInstruction(1L))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(2L))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(3L))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(4L))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(5L))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(6L))

        // Float
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(-1f))
        assertEquals(JVMInstruction.FCONST_0, ASMUtils.getLoadConstInstruction(0f))
        assertEquals(JVMInstruction.FCONST_1, ASMUtils.getLoadConstInstruction(1f))
        assertEquals(JVMInstruction.FCONST_2, ASMUtils.getLoadConstInstruction(2f))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(3f))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(4f))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(5f))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(6f))

        // Double
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(-1.0))
        assertEquals(JVMInstruction.DCONST_0, ASMUtils.getLoadConstInstruction(0.0))
        assertEquals(JVMInstruction.DCONST_1, ASMUtils.getLoadConstInstruction(1.0))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(2.0))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(3.0))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(4.0))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(5.0))
        assertEquals(JVMInstruction.LDC, ASMUtils.getLoadConstInstruction(6.0))
    }

    @Test
    fun getPrimitiveCastInstruction() {
        assertThrowsExactly(IllegalArgumentException::class.java) {
            assertEquals(JVMInstruction.I2L, ASMUtils.getPrimitiveCastInstruction(String::class.java, Long::class.java))
        }

        assertThrowsExactly(IllegalArgumentException::class.java) {
            assertEquals(JVMInstruction.I2L, ASMUtils.getPrimitiveCastInstruction(Long::class.java, String::class.java))
        }

        assertThrowsExactly(IllegalArgumentException::class.java) {
            assertEquals(JVMInstruction.I2L, ASMUtils.getPrimitiveCastInstruction(Long::class.java, Long::class.java))
        }

        assertEquals(JVMInstruction.I2L, ASMUtils.getPrimitiveCastInstruction(Int::class.java, Long::class.java))
        assertEquals(JVMInstruction.I2F, ASMUtils.getPrimitiveCastInstruction(Int::class.java, Float::class.java))
        assertEquals(JVMInstruction.I2D, ASMUtils.getPrimitiveCastInstruction(Int::class.java, Double::class.java))
        assertEquals(JVMInstruction.I2B, ASMUtils.getPrimitiveCastInstruction(Int::class.java, Byte::class.java))
        assertEquals(JVMInstruction.I2C, ASMUtils.getPrimitiveCastInstruction(Int::class.java, Char::class.java))
        assertEquals(JVMInstruction.I2S, ASMUtils.getPrimitiveCastInstruction(Int::class.java, Short::class.java))

        assertEquals(JVMInstruction.L2I, ASMUtils.getPrimitiveCastInstruction(Long::class.java, Int::class.java))
        assertEquals(JVMInstruction.L2F, ASMUtils.getPrimitiveCastInstruction(Long::class.java, Float::class.java))
        assertEquals(JVMInstruction.L2D, ASMUtils.getPrimitiveCastInstruction(Long::class.java, Double::class.java))

        assertEquals(JVMInstruction.F2I, ASMUtils.getPrimitiveCastInstruction(Float::class.java, Int::class.java))
        assertEquals(JVMInstruction.F2L, ASMUtils.getPrimitiveCastInstruction(Float::class.java, Long::class.java))
        assertEquals(JVMInstruction.F2D, ASMUtils.getPrimitiveCastInstruction(Float::class.java, Double::class.java))

        assertEquals(JVMInstruction.D2I, ASMUtils.getPrimitiveCastInstruction(Double::class.java, Int::class.java))
        assertEquals(JVMInstruction.D2L, ASMUtils.getPrimitiveCastInstruction(Double::class.java, Long::class.java))
        assertEquals(JVMInstruction.D2F, ASMUtils.getPrimitiveCastInstruction(Double::class.java, Float::class.java))

        assertThrowsExactly(IllegalArgumentException::class.java) {
            assertEquals(JVMInstruction.I2L, ASMUtils.getPrimitiveCastInstruction(Byte::class.java, Long::class.java))
            assertEquals(JVMInstruction.I2L, ASMUtils.getPrimitiveCastInstruction(Char::class.java, Long::class.java))
            assertEquals(JVMInstruction.I2L, ASMUtils.getPrimitiveCastInstruction(Short::class.java, Long::class.java))
        }
    }

    @Test
    fun getCalculationInstruction() {
        assertEquals(JVMInstruction.IADD, ASMUtils.getCalculationInstruction(CalculateType.ADD, Int::class.java))
        assertEquals(JVMInstruction.LADD, ASMUtils.getCalculationInstruction(CalculateType.ADD, Long::class.java))
        assertEquals(JVMInstruction.FADD, ASMUtils.getCalculationInstruction(CalculateType.ADD, Float::class.java))
        assertEquals(JVMInstruction.DADD, ASMUtils.getCalculationInstruction(CalculateType.ADD, Double::class.java))

        assertEquals(JVMInstruction.IS, ASMUtils.getCalculationInstruction(CalculateType.SUBTRACT, Int::class.java))
        assertEquals(JVMInstruction.LS, ASMUtils.getCalculationInstruction(CalculateType.SUBTRACT, Long::class.java))
        assertEquals(JVMInstruction.FS, ASMUtils.getCalculationInstruction(CalculateType.SUBTRACT, Float::class.java))
        assertEquals(JVMInstruction.DS, ASMUtils.getCalculationInstruction(CalculateType.SUBTRACT, Double::class.java))

        assertEquals(JVMInstruction.IMUL, ASMUtils.getCalculationInstruction(CalculateType.MULTIPLY, Int::class.java))
        assertEquals(JVMInstruction.LMUL, ASMUtils.getCalculationInstruction(CalculateType.MULTIPLY, Long::class.java))
        assertEquals(JVMInstruction.FMUL, ASMUtils.getCalculationInstruction(CalculateType.MULTIPLY, Float::class.java))
        assertEquals(JVMInstruction.DMUL, ASMUtils.getCalculationInstruction(CalculateType.MULTIPLY, Double::class.java))

        assertEquals(JVMInstruction.IDIV, ASMUtils.getCalculationInstruction(CalculateType.DIVIDE, Int::class.java))
        assertEquals(JVMInstruction.LDIV, ASMUtils.getCalculationInstruction(CalculateType.DIVIDE, Long::class.java))
        assertEquals(JVMInstruction.FDIV, ASMUtils.getCalculationInstruction(CalculateType.DIVIDE, Float::class.java))
        assertEquals(JVMInstruction.DDIV, ASMUtils.getCalculationInstruction(CalculateType.DIVIDE, Double::class.java))

        assertEquals(JVMInstruction.IREM, ASMUtils.getCalculationInstruction(CalculateType.REM, Int::class.java))
        assertEquals(JVMInstruction.LREM, ASMUtils.getCalculationInstruction(CalculateType.REM, Long::class.java))
        assertEquals(JVMInstruction.FREM, ASMUtils.getCalculationInstruction(CalculateType.REM, Float::class.java))
        assertEquals(JVMInstruction.DREM, ASMUtils.getCalculationInstruction(CalculateType.REM, Double::class.java))

        assertEquals(JVMInstruction.INEG, ASMUtils.getCalculationInstruction(CalculateType.NEGATIVE, Int::class.java))
        assertEquals(JVMInstruction.LNEG, ASMUtils.getCalculationInstruction(CalculateType.NEGATIVE, Long::class.java))
        assertEquals(JVMInstruction.FNEG, ASMUtils.getCalculationInstruction(CalculateType.NEGATIVE, Float::class.java))
        assertEquals(JVMInstruction.DNEG, ASMUtils.getCalculationInstruction(CalculateType.NEGATIVE, Double::class.java))

        assertEquals(JVMInstruction.ISHL, ASMUtils.getCalculationInstruction(CalculateType.SHIFT_LEFT, Int::class.java))
        assertEquals(JVMInstruction.LSHL, ASMUtils.getCalculationInstruction(CalculateType.SHIFT_LEFT, Long::class.java))

        assertEquals(JVMInstruction.ISHR, ASMUtils.getCalculationInstruction(CalculateType.SHIFT_RIGHT, Int::class.java))
        assertEquals(JVMInstruction.LSHR, ASMUtils.getCalculationInstruction(CalculateType.SHIFT_RIGHT, Long::class.java))

        assertEquals(JVMInstruction.IUSHR, ASMUtils.getCalculationInstruction(CalculateType.U_SHIFT_RIGHT, Int::class.java))
        assertEquals(JVMInstruction.LUSHR, ASMUtils.getCalculationInstruction(CalculateType.U_SHIFT_RIGHT, Long::class.java))

        assertEquals(JVMInstruction.IAND, ASMUtils.getCalculationInstruction(CalculateType.AND, Int::class.java))
        assertEquals(JVMInstruction.LAND, ASMUtils.getCalculationInstruction(CalculateType.AND, Long::class.java))

        assertEquals(JVMInstruction.IOR, ASMUtils.getCalculationInstruction(CalculateType.OR, Int::class.java))
        assertEquals(JVMInstruction.LOR, ASMUtils.getCalculationInstruction(CalculateType.OR, Long::class.java))

        assertEquals(JVMInstruction.IXOR, ASMUtils.getCalculationInstruction(CalculateType.XOR, Int::class.java))
        assertEquals(JVMInstruction.LXOR, ASMUtils.getCalculationInstruction(CalculateType.XOR, Long::class.java))
    }
}