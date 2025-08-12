package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.reflect.BaseDataType
import com.lovelycatv.vertex.reflect.TypeUtils
import java.lang.reflect.Method
import java.lang.reflect.Type
import kotlin.reflect.KClass

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
        return TypeUtils.isPrimitiveType(originalClass)
    }

    companion object {
         private val CACHE_MAP = mutableMapOf<Class<*>, TypeDeclaration>()

        val CLASS = fromClass(Class::class.java)
        val OBJECT = fromClass(ASMUtils.OBJECT_CLASS)
        val STRING = fromClass(String::class.java)
        val METHOD = fromClass(Method::class.java)

        val VOID = fromClass(Void.TYPE)
        val PACKAGED_VOID = fromClass(Void::class.java)

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

        @JvmStatic
        fun fromName(className: String): TypeDeclaration {
            return if (className == "void") VOID
                else if (BaseDataType.PRIMITIVE_TYPES.contains(className))
                    fromClass(BaseDataType.getPrimitiveTypeClassByName(className))
                else
                    fromClass(Class.forName(className))
        }

        @JvmStatic
        fun fromClass(clazz: Class<*>): TypeDeclaration {
            return CACHE_MAP.computeIfAbsent(clazz) {
                if (clazz.isArray) {
                    // Recursive get component type
                    TypeDeclaration(TypeUtils.getArrayComponent(clazz), true, TypeUtils.getArrayDimensions(clazz), clazz)
                } else {
                    TypeDeclaration(clazz, false, 1)
                }
            }
        }

        fun fromClass(kClass: KClass<*>): TypeDeclaration {
            return this.fromClass(kClass.java)
        }

        @JvmStatic
        fun fromClasses(vararg classes: Class<*>): Array<TypeDeclaration> {
            return classes.map { this.fromClass(it) }.toTypedArray()
        }

        fun fromClasses(vararg kClasses: KClass<*>): Array<TypeDeclaration> {
            return this.fromClasses(*kClasses.map { it.java }.toTypedArray())
        }
    }

    fun getDescriptor(): String {
        return if (this.isArray) {
            TypeUtils.getArrayDescriptor(this.type, this.arrayDimensions)
        } else {
            TypeUtils.getDescriptor(this.originalClass)
        }
    }

    fun getInternalClassName(): String {
        return TypeUtils.getInternalName(this.originalClass)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TypeDeclaration) {
            return false
        }

        return this.originalClass == other.originalClass
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + isArray.hashCode()
        result = 31 * result + arrayDimensions
        result = 31 * result + originalClass.hashCode()
        return result
    }
}