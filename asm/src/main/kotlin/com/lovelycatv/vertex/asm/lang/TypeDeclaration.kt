package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.reflect.ReflectUtils
import java.lang.reflect.Type

/**
 * @author lovelycat
 * @since 2025-08-01 18:39
 * @version 1.0
 */
open class TypeDeclaration(
    val type: Class<*>,
    val isArray: Boolean = false,
    val arrayDimensions: Int = 1,
    val originalClass: Class<*> = type
) : Type by type {
    fun isPrimitiveType(): Boolean {
        return !this.isArray && !type.isArray && type.isPrimitive
    }

    companion object {
        val OBJECT = TypeDeclaration(ASMUtils.OBJECT_CLASS, false, 1)
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
            return if (clazz.isArray) {
                TypeDeclaration(clazz.componentType, true, ReflectUtils.getArrayDimensions(clazz), clazz)
            } else {
                TypeDeclaration(clazz, false, 1)
            }
        }
    }

    fun getDescriptor(): String {
        return if (this.isArray) {
            ASMUtils.getArrayDescriptor(this.type, this.arrayDimensions)
        } else {
            ASMUtils.getDescriptor(this.originalClass)
        }
    }

    fun getInternalClassName(): String {
        return ASMUtils.getInternalName(this.originalClass)
    }
}