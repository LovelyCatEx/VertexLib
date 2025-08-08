package com.lovelycatv.vertex.reflect

import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-08-08 10:38
 * @version 1.0
 */
object TypeUtils {
    @JvmStatic
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
            Array::class.java -> {
                val elementType = getArrayComponent(clazz)
                val dimensions = getArrayDimensions(clazz)
                this.getArrayDescriptor(elementType, dimensions)
            }
            else -> "L${clazz.canonicalName.replace(".", "/")};"
        }
    }

    @JvmStatic
    fun getInternalName(clazz: Class<*>): String {
        return clazz.canonicalName.replace(".", "/")
    }

    @JvmStatic
    fun getArrayDescriptor(elementClazz: Class<*>, dimensions: Int = 1): String {
        return "[".repeat(dimensions) + this.getDescriptor(elementClazz)
    }

    @JvmStatic
    fun isVoid(clazz: Class<*>): Boolean {
        return clazz == Void::class.java || clazz == Void.TYPE
    }

    fun isPrimitiveType(clazz: KClass<*>, includingPackagedType: Boolean = false): Boolean {
        return isPrimitiveType(clazz.java, includingPackagedType)
    }

    @JvmStatic
    fun isPrimitiveType(clazz: Class<*>, includingPackagedType: Boolean = false): Boolean {
        return !clazz.isArray && clazz.canonicalName in if (!includingPackagedType)
            BaseDataType.PRIMITIVE_TYPES
        else
            BaseDataType.ALL
    }

    @JvmStatic
    fun getPackagedPrimitiveType(clazz: Class<*>): Class<*> {
        return Class.forName(when (clazz) {
            Int::class.java -> BaseDataType.PACKAGED_INTEGER
            Short::class.java -> BaseDataType.PACKAGED_SHORT
            Long::class.java -> BaseDataType.PACKAGED_LONG
            Float::class.java -> BaseDataType.PACKAGED_FLOAT
            Double::class.java -> BaseDataType.PACKAGED_DOUBLE
            Byte::class.java -> BaseDataType.PACKAGED_BYTE
            Char::class.java -> BaseDataType.PACKAGED_CHAR
            Boolean::class.java -> BaseDataType.PACKAGED_BOOLEAN
            else -> throw IllegalArgumentException("Class ${clazz.canonicalName} is not a primitive type")
        })
    }

    fun getArrayClass(elementType: Class<*>, dimension: Int): Class<*> {
        return if (this.isPrimitiveType(elementType)) {
            Class.forName("[".repeat(dimension) + when (elementType) {
                Byte::class.java -> "B"
                Boolean::class.java -> "Z"
                Void::class.java, Void.TYPE -> "V"
                Char::class.java -> "C"
                Short::class.java -> "S"
                Int::class.java -> "I"
                Long::class.java -> "J"
                Float::class.java -> "F"
                Double::class.java -> "D"
                else -> throw IllegalStateException("${elementType.canonicalName} should be a supported primitive type.")
            })
        } else if (elementType.isArray) {
            val realElementType = this.getArrayComponent(elementType)
            val realDimensions = this.getArrayDimensions(elementType) + dimension
            this.getArrayClass(realElementType, realDimensions)
        } else {
            Class.forName("[".repeat(dimension) + "L${elementType.canonicalName};")
        }
    }

    @JvmStatic
    fun getArrayDimensions(clazz: Class<*>): Int {
        require(clazz.isArray)

        var tClazz = clazz
        var dimension = 0
        while (tClazz.isArray) {
            dimension++
            tClazz = tClazz.componentType
        }
        return dimension
    }

    @JvmStatic
    fun getArrayComponent(clazz: Class<*>): Class<*> {
        require(clazz.isArray)

        var componentClazz = clazz.componentType
        while (componentClazz.isArray) {
            componentClazz = componentClazz.componentType
        }
        return componentClazz
    }
}