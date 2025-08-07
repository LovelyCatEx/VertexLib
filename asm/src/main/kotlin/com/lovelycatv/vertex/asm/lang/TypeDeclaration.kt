package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.reflect.BaseDataType
import com.lovelycatv.vertex.reflect.ReflectUtils
import java.lang.reflect.Type
import java.sql.Ref

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
        return ReflectUtils.isPrimitiveType(originalClass)
    }

    companion object {
        private val CACHE_MAP = mutableMapOf<Class<*>, TypeDeclaration>()

        val OBJECT = TypeDeclaration(ASMUtils.OBJECT_CLASS, false, 1)
        val STRING = fromClass(String::class.java)

        val VOID = TypeDeclaration(Void::class.java, false, 1)

        val BYTE = fromClass(BaseDataType.BYTE_CLASS)
        val BOOLEAN = fromClass(BaseDataType.BYTE_CLASS)
        val CHAR = fromClass(BaseDataType.CHAR_CLASS)
        val SHORT = fromClass(BaseDataType.SHORT_CLASS)
        val INT = fromClass(BaseDataType.INTEGER_CLASS)
        val LONG = fromClass(BaseDataType.LONG_CLASS)
        val FLOAT = fromClass(BaseDataType.FLOAT_CLASS)
        val DOUBLE = fromClass(BaseDataType.DOUBLE_CLASS)

        val PACKAGED_BYTE = fromClass(BaseDataType.PACKAGED_BYTE_CLASS)
        val PACKAGED_BOOLEAN = fromClass(BaseDataType.PACKAGED_BOOLEAN_CLASS)
        val PACKAGED_CHAR = fromClass(BaseDataType.PACKAGED_CHAR_CLASS)
        val PACKAGED_SHORT = fromClass(BaseDataType.PACKAGED_SHORT_CLASS)
        val PACKAGED_INT = fromClass(BaseDataType.PACKAGED_INTEGER_CLASS)
        val PACKAGED_LONG = fromClass(BaseDataType.PACKAGED_LONG_CLASS)
        val PACKAGED_FLOAT = fromClass(BaseDataType.PACKAGED_FLOAT_CLASS)
        val PACKAGED_DOUBLE = fromClass(BaseDataType.PACKAGED_DOUBLE_CLASS)

        fun fromName(className: String): TypeDeclaration {
            return fromClass(Class.forName(className))
        }

        fun fromClass(clazz: Class<*>): TypeDeclaration {
            return CACHE_MAP.computeIfAbsent(clazz) {
                if (clazz.isArray) {
                    // Recursive get component type
                    TypeDeclaration(ReflectUtils.getArrayComponent(clazz), true, ReflectUtils.getArrayDimensions(clazz), clazz)
                } else {
                    TypeDeclaration(clazz, false, 1)
                }
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