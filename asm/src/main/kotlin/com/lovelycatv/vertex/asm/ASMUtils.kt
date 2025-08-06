package com.lovelycatv.vertex.asm

import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import org.objectweb.asm.Opcodes

/**
 * @author lovelycat
 * @since 2025-08-01 17:52
 * @version 1.0
 */
object ASMUtils {
    val OBJECT_CLASS = java.lang.Object::class.java
    const val OBJECT_INTERNAL_NAME = "java/lang/Object"
    const val OBJECT_DESCRIPTOR = "Ljava/lang/Object;"
    const val CONSTRUCTOR_NAME = "<init>"

    fun countSlots(clazz: Class<*>): Int {
        return when (clazz) {
            Double::class.java, Long::class.java -> 2
            else -> 1
        }
    }

    fun getLoadConstInstruction(value: Any): JVMInstruction {
        return when (value) {
            is Boolean -> if (value) JVMInstruction.ICONST_1 else JVMInstruction.ICONST_0

            is Char, is Byte, is Short, is Int -> when (val intValue = if (value is Char) value.code else value.toString().toInt()) {
                in (-1..5) -> when (intValue) {
                    -1 -> JVMInstruction.ICONST_M1
                    0 -> JVMInstruction.ICONST_0
                    1 -> JVMInstruction.ICONST_1
                    2 -> JVMInstruction.ICONST_2
                    3 -> JVMInstruction.ICONST_3
                    4 -> JVMInstruction.ICONST_4
                    5 -> JVMInstruction.ICONST_5
                    else -> throw IllegalStateException("Impossible :(")
                }
                in (-128..127) -> JVMInstruction.BIPUSH
                in (-32768..32767) -> JVMInstruction.SIPUSH
                else -> JVMInstruction.LDC
            }


            is Long -> when (value) {
                0L -> JVMInstruction.LCONST_0
                1L -> JVMInstruction.LCONST_1
                else -> JVMInstruction.LDC
            }

            is Float -> when (value) {
                0f -> JVMInstruction.FCONST_0
                1f -> JVMInstruction.FCONST_1
                2f -> JVMInstruction.FCONST_2
                else -> JVMInstruction.LDC
            }

            is Double -> when (value) {
                0.0 -> JVMInstruction.DCONST_0
                1.0 -> JVMInstruction.DCONST_1
                else -> JVMInstruction.LDC
            }

            else -> JVMInstruction.LDC
        }
    }

    fun getLoadOpcode(type: TypeDeclaration): JVMInstruction {
        return if (type.isArray) {
            JVMInstruction.ALOAD
        } else {
            when (type.originalClass) {
                Void.TYPE, Void::class.java -> throw IllegalArgumentException("void type has no load opcode")
                Boolean::class.java,
                Byte::class.java,
                Char::class.java,
                Short::class.java,
                Int::class.java -> JVMInstruction.ILOAD

                Long::class.java -> JVMInstruction.LLOAD
                Float::class.java -> JVMInstruction.FLOAD
                Double::class.java -> JVMInstruction.DLOAD

                else -> JVMInstruction.ALOAD
            }
        }
    }

    fun getLoadOpcode(clazz: Class<*>): JVMInstruction {
        return this.getLoadOpcode(TypeDeclaration.fromClass(clazz))
    }

    fun getLoadOpcodeForArrayValue(elementClazz: Class<*>): JVMInstruction {
        return when (elementClazz) {
            Boolean::class.java,
            Byte::class.java -> JVMInstruction.BALOAD
            Char::class.java -> JVMInstruction.CALOAD
            Short::class.java -> JVMInstruction.SALOAD
            Int::class.java -> JVMInstruction.IALOAD
            Long::class.java -> JVMInstruction.LALOAD
            Float::class.java -> JVMInstruction.FALOAD
            Double::class.java -> JVMInstruction.DALOAD

            else -> JVMInstruction.AALOAD
        }
    }

    fun getStoreOpcode(type: TypeDeclaration): JVMInstruction {
        return if (type.isArray) {
            JVMInstruction.ASTORE
        } else {
            when (type.originalClass) {
                Void.TYPE, Void::class.java -> throw IllegalArgumentException("void type has no save opcode")
                Boolean::class.java,
                Byte::class.java,
                Char::class.java,
                Short::class.java,
                Int::class.java -> JVMInstruction.ISTORE

                Long::class.java -> JVMInstruction.LSTORE
                Float::class.java -> JVMInstruction.FSTORE
                Double::class.java -> JVMInstruction.DSTORE

                else -> JVMInstruction.ASTORE
            }
        }
    }

    fun getStoreOpcode(clazz: Class<*>): JVMInstruction {
        return this.getStoreOpcode(TypeDeclaration.fromClass(clazz))
    }

    fun getStoreOpcodeForArrayValue(elementClazz: Class<*>): JVMInstruction {
        return when (elementClazz) {
            Boolean::class.java,
            Byte::class.java -> JVMInstruction.BASTORE
            Char::class.java -> JVMInstruction.CASTORE
            Short::class.java -> JVMInstruction.SASTORE
            Int::class.java -> JVMInstruction.IASTORE
            Long::class.java -> JVMInstruction.LASTORE
            Float::class.java -> JVMInstruction.FASTORE
            Double::class.java -> JVMInstruction.DASTORE

            else -> JVMInstruction.AASTORE
        }
    }

    fun getReturnOpcode(clazz: Class<*>): JVMInstruction {
        return when (clazz) {
            Void.TYPE, Void::class.java -> JVMInstruction.RETURN
            Boolean::class.java,
            Byte::class.java,
            Char::class.java,
            Short::class.java,
            Int::class.java -> JVMInstruction.IRETURN

            Long::class.java -> JVMInstruction.LRETURN
            Float::class.java -> JVMInstruction.FRETURN
            Double::class.java -> JVMInstruction.DRETURN

            else -> JVMInstruction.ARETURN
        }
    }

    fun getNewArrayOpcode(clazz: Class<*>, dimensions: Int): JVMInstruction {
        require(dimensions > 0)

        return if (dimensions == 1) {
            if (this.isPrimitiveType(clazz)) {
                JVMInstruction.NEWARRAY
            } else {
                JVMInstruction.ANEWARRAY
            }
        } else {
            JVMInstruction.MULTIANEWARRAY
        }
    }

    fun getOperandForNewPrimitiveArray(clazz: Class<*>): JVMInstruction {
        return when (clazz) {
            Int::class.java -> JVMInstruction.T_INT
            Short::class.java -> JVMInstruction.T_SHORT
            Long::class.java -> JVMInstruction.T_LONG
            Float::class.java -> JVMInstruction.T_FLOAT
            Double::class.java -> JVMInstruction.T_DOUBLE
            Char::class.java -> JVMInstruction.T_CHAR
            Byte::class.java -> JVMInstruction.T_BYTE
            Boolean::class.java -> JVMInstruction.T_BOOLEAN
            else -> throw IllegalArgumentException("Type ${clazz.canonicalName} is not a primitive type")
        }
    }


    fun isPrimitiveType(clazz: Class<*>): Boolean {
        return !clazz.isArray && when (clazz) {
            Void.TYPE, Void::class.java -> true
            Boolean::class.java -> true
            Byte::class.java -> true
            Char::class.java -> true
            Short::class.java -> true
            Int::class.java -> true
            Long::class.java -> true
            Float::class.java -> true
            Double::class.java -> true
            else -> false
        }
    }

    fun getDescriptor(clazz: Class<*>): String {
        return when (clazz) {
            Void.TYPE, Void::class.java -> "V"
            Boolean::class.java -> "Z"
            Byte::class.java -> "B"
            Char::class.java -> "C"
            Short::class.java -> "S"
            Int::class.java -> "I"
            Long::class.java -> "J"
            Float::class.java -> "F"
            Double::class.java -> "D"
            Array::class.java -> "[Ljava/lang/Object;"
            else -> "L${clazz.canonicalName.replace(".", "/")};"
        }
    }

    fun getInternalName(clazz: Class<*>): String {
        return clazz.canonicalName.replace(".", "/")
    }

    fun getArrayDescriptor(elementClazz: Class<*>, dimensions: Int = 1): String {
        return "[".repeat(dimensions) + this.getDescriptor(elementClazz)
    }

    fun toAccessCode(keyword: IJavaKeyWord): Int {
        return when (keyword.getWord()) {
            JavaModifier.PUBLIC.name -> Opcodes.ACC_PUBLIC
            JavaModifier.PRIVATE.name -> Opcodes.ACC_PRIVATE
            JavaModifier.PROTECTED.name -> Opcodes.ACC_PROTECTED
            JavaModifier.FINAL.name -> Opcodes.ACC_FINAL
            JavaModifier.ABSTRACT.name -> Opcodes.ACC_ABSTRACT
            JavaModifier.SEALED.name -> 0
            JavaModifier.NON_SEALED.name -> 0
            JavaModifier.STATIC.name -> Opcodes.ACC_STATIC
            JavaModifier.VOLATILE.name -> Opcodes.ACC_VOLATILE
            JavaModifier.SYNCHRONIZED.name -> Opcodes.ACC_SYNCHRONIZED
            JavaModifier.TRANSIENT.name -> Opcodes.ACC_TRANSIENT
            JavaModifier.NATIVE.name -> Opcodes.ACC_NATIVE
            JavaModifier.STRICTFP.name -> Opcodes.ACC_STRICT
            JavaKeyWord.ENUM.name -> Opcodes.ACC_ENUM
            JavaKeyWord.RECORD.name -> Opcodes.ACC_RECORD
            JavaKeyWord.INTERFACE.name -> Opcodes.ACC_INTERFACE
            JavaKeyWord.SUPER.name -> Opcodes.ACC_SUPER
            else -> 0
        }
    }
}