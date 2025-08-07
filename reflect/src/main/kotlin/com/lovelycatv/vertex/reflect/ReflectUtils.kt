package com.lovelycatv.vertex.reflect

import com.lovelycatv.vertex.log.logger
import java.lang.Exception
import java.lang.reflect.Array
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.superclasses

object ReflectUtils {
    private val logger = logger()

    private val SHALLOW_COPY_FIELD_VALUE_TRANSFORMER: (Any?) -> Any? = { it }
    private val DEEP_COPY_FIELD_VALUE_TRANSFORMER: (Any?) -> Any? = {
        if (it != null) {
            this.deepCopy(it)
        } else {
            null
        }
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

    fun isVoid(clazz: Class<*>): Boolean {
        return clazz == Void::class.java || clazz == Void.TYPE
    }

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

    fun getArrayComponent(clazz: Class<*>): Class<*> {
        require(clazz.isArray)

        var componentClazz = clazz.componentType
        while (componentClazz.isArray) {
            componentClazz = componentClazz.componentType
        }
        return componentClazz
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

    /**
     * Get all declared fields (including parent's)
     *
     * @param target Object
     * @return All declared field
     */
    @JvmStatic
    fun getAllDeclaredFields(target: Any): List<Field> {
        val result = mutableListOf<Field>()
        var clazz: Class<*>? = target::class.java
        while (clazz != null && clazz != java.lang.Object::class.java) {
            result.addAll(clazz.declaredFields)
            clazz = clazz.superclass
        }
        return result
    }

    /**
     * Get all declared properties (including parent's)
     *
     * See also: [getAllDeclaredFields]
     *
     * @param target Object
     * @return All declared properties.
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun getAllDeclaredProperties(target: Any): List<KProperty1<Any, *>> {
        val result = mutableListOf<KProperty1<Any, *>>()
        var clazz: KClass<*>? = target::class
        while (clazz != null && clazz != Any::class) {
            result.addAll(clazz.declaredMemberProperties.map { it as KProperty1<Any, *> })
            clazz = clazz.superclasses.find { !it.java.isInterface }
        }
        return result
    }

    /**
     * Copy an object.
     *
     * @param T Type of the object to be copied.
     * @param target Object to be copied.
     * @param fieldValueTransformer After a new instance of the object created,
     *                              determine how to assign field value to this new instance.
     * @return New instance of the object.
     */
    @JvmStatic
    @Throws(exceptionClasses = [NullPointerException::class])
    fun <T: Any> copyObject(target: T, fieldValueTransformer: (Any?) -> Any?): T {
        val targetClazz = target::class.java

        if (isPrimitiveType(targetClazz, true))
            return target

        @Suppress("UNCHECKED_CAST")
        return if (targetClazz.isArray) {
            val componentType = targetClazz.componentType
            val length = Array.getLength(target)
            val copiedArray = Array.newInstance(componentType, length)
            for (i in 0..<length) {
                Array.set(copiedArray, i, fieldValueTransformer.invoke(Array.get(target, i)))
            }
            copiedArray as T
        } else if (Collection::class.java.isAssignableFrom(targetClazz)) {
            val collection = target as MutableCollection<*>
            val copiedCollection = (targetClazz.constructors.find { it.parameterCount == 0 }?.newInstance()
                ?: throw IllegalStateException("Target is a Collection but noArgConstructor not found.")) as MutableCollection<Any?>
            collection.forEach {
                copiedCollection.add(fieldValueTransformer.invoke(it))
            }
            copiedCollection as T
        } else {
            val noArgConstructor = targetClazz.constructors.find { it.parameterCount == 0 }
                ?: throw IllegalStateException("NoArgConstructor not found in ${targetClazz.canonicalName}.")

            @Suppress("UNCHECKED_CAST")
            val newInstance = noArgConstructor.newInstance() as T

            targetClazz.declaredFields.forEach {
                try {
                    it.isAccessible = true
                    it.set(newInstance, fieldValueTransformer.invoke(it.get(target)))
                    it.isAccessible = false
                } catch (e: Exception) {
                    logger.warn("Unable to copy field ${it.name}, type: ${it.type}", e)
                }

            }

            newInstance
        }
    }

    @JvmStatic
    @Throws(exceptionClasses = [NullPointerException::class])
    fun <T: Any> shallowCopy(target: T): T {
        return this.copyObject(target, SHALLOW_COPY_FIELD_VALUE_TRANSFORMER)
    }

    @JvmStatic
    @Throws(exceptionClasses = [NullPointerException::class])
    fun <T: Any> deepCopy(target: T): T {
        return this.copyObject(target, DEEP_COPY_FIELD_VALUE_TRANSFORMER)
    }

    @JvmStatic
    fun storeObjectFields(target: Any, includingParent: Boolean): Map<String, Any?> {
        return (if (includingParent) getAllDeclaredFields(target) else target::class.java.declaredFields.toList()).associate {
            it.isAccessible = true
            val t = it.name to it.get(target)
            it.isAccessible = false
            t
        }
    }

    @JvmStatic
    fun <T: Any> restoreObjectFields(target: T, fieldWithValueMap: Map<String, Any?>, includingParent: Boolean): T {
        (if (includingParent) getAllDeclaredFields(target) else target::class.java.declaredFields.toList())
            .filter { it.name in fieldWithValueMap.keys }
            .forEach {
                it.isAccessible = true
                it.set(target, fieldWithValueMap[it.name])
                it.isAccessible = false
            }

        return target
    }
}