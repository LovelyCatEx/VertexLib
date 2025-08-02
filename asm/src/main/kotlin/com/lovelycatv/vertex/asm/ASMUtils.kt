package com.lovelycatv.vertex.asm

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

    fun getLoadOpcode(clazz: Class<*>): JVMInstruction {
        return when (clazz) {
            Void::class.java -> throw IllegalArgumentException("void type has no load opcode")
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

    fun getStoreOpcode(clazz: Class<*>): JVMInstruction {
        return when (clazz) {
            Void::class.java -> throw IllegalArgumentException("void type has no save opcode")
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

    fun getReturnOpcode(clazz: Class<*>): JVMInstruction {
        return when (clazz) {
            Void::class.java -> JVMInstruction.RETURN
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


    fun isPrimitiveType(clazz: Class<*>): Boolean {
        return when (clazz) {
            Void::class.java -> true
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
            Void::class.java -> "V"
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