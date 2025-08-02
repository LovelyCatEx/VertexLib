package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.ASMUtils
import java.lang.reflect.Type

/**
 * @author lovelycat
 * @since 2025-08-01 18:39
 * @version 1.0
 */
open class TypeDeclaration(
    val type: Class<*>,
    val isArray: Boolean = false,
    val arrayDimensions: Int = 1
) : Type by type {
    companion object {
        val VOID = TypeDeclaration(Void::class.java, false, 1)
        val STRING = fromClass(String::class.java)
        val SHORT = fromClass(Short::class.java)
        val INT = fromClass(Int::class.java)
        val LONG = fromClass(Long::class.java)
        val FLOAT = fromClass(Float::class.java)
        val DOUBLE = fromClass(Double::class.java)

        fun fromName(className: String): TypeDeclaration {
            return fromClass(Class.forName(className))
        }

        fun fromClass(clazz: Class<*>): TypeDeclaration {
            return TypeDeclaration(clazz)
        }
    }

    fun getDescriptor(): String {
        return if (this.isArray) {
            ASMUtils.getArrayDescriptor(this.type, this.arrayDimensions)
        } else {
            ASMUtils.getDescriptor(this.type)
        }
    }

    fun getInternalClassName(): String {
        return ASMUtils.getInternalName(this.type)
    }
}