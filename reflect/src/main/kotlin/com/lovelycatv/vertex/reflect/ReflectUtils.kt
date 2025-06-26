package com.lovelycatv.vertex.reflect

import java.lang.reflect.Array
import kotlin.jvm.Throws
import kotlin.reflect.KClass

object ReflectUtils {
    private val SHALLOW_COPY_FIELD_VALUE_TRANSFORMER: (Any?) -> Any? = { it }
    private val DEEP_COPY_FIELD_VALUE_TRANSFORMER: (Any?) -> Any? = {
        if (it != null) {
            this.deepCopy(it)
        } else {
            null
        }
    }

    fun isPrimitiveType(clazz: KClass<*>, includingPackagedType: Boolean = false): Boolean {
        return isPrimitiveType(clazz.java, includingPackagedType)
    }

    @JvmStatic
    fun isPrimitiveType(clazz: Class<*>, includingPackagedType: Boolean = false): Boolean {
        return clazz.canonicalName in if (!includingPackagedType)
            BaseDataType.PRIMITIVE_TYPES
        else
            BaseDataType.ALL
    }

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
                it.isAccessible = true
                it.set(newInstance, fieldValueTransformer.invoke(it.get(target)))
                it.isAccessible = false
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
}