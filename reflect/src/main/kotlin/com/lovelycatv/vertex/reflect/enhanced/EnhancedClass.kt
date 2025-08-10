package com.lovelycatv.vertex.reflect.enhanced

import com.lovelycatv.vertex.reflect.MethodSignature
import com.lovelycatv.vertex.reflect.enhanced.factory.EnhancedClassFactory
import com.lovelycatv.vertex.reflect.enhanced.factory.JavaEnhancedClassFactory
import com.lovelycatv.vertex.reflect.enhanced.factory.NativeEnhancedClassFactory
import java.lang.reflect.Modifier

/**
 * @author lovelycat
 * @since 2025-08-08 15:34
 * @version 1.0
 */
abstract class EnhancedClass(val originalClass: Class<*>) {
    protected val signatureToIndex: MutableMap<MethodSignature, Int> = mutableMapOf()

    protected val originalConstructors = originalClass.constructors
    protected val originalMethods = originalClass.declaredMethods.filter { !Modifier.isFinal(it.modifiers) && !it.isSynthetic }
    protected val originalFields = originalClass.fields

    fun getIndex(methodName: String, vararg parameters: Class<*>): Int {
        val signature = MethodSignature(methodName, *parameters)

        return this.signatureToIndex[signature]
            ?: throw NoSuchMethodException("Method $signature not found in ${originalClass.canonicalName}.")
    }

    fun getConstructorIndex(vararg parameters: Class<*>): Int {
        return this.getIndex("<init>", *parameters)
    }

    fun getMethod(methodName: String, vararg parameters: Class<*>): EnhancedMethod {
        return EnhancedMethod(this, this.getIndex(methodName, *parameters))
    }

    fun getConstructor(vararg parameters: Class<*>): EnhancedConstructor {
        return EnhancedConstructor(this, this.getConstructorIndex(*parameters))
    }


    abstract fun invokeMethod(target: Any?, index: Int, vararg args: Any?): Any?


    companion object {
        private val CACHE_MAP: MutableMap<Class<*>, EnhancedClass> = mutableMapOf()

        fun createNative(targetClass: Class<*>, forceRebuild: Boolean = false): NativeEnhancedClass {
            return this.create(NativeEnhancedClassFactory(), targetClass, forceRebuild)
        }

        fun createJava(targetClass: Class<*>, forceRebuild: Boolean = false): JavaEnhancedClass {
            return this.create(JavaEnhancedClassFactory(), targetClass, forceRebuild)
        }

        /**
         * Creates or retrieves the [JavaEnhancedClass] instance for the given target class.
         *
         * If an instance already exists in the cache, it returns that instance.
         * Otherwise, it creates a new one using [JavaEnhancedClassFactory] and caches it.
         *
         * @param targetClass The target class to enhance.
         * @param forceRebuild If true, the target EnhancedClass will be rebuilt and replace old one in cache.
         * @return The [JavaEnhancedClass] instance corresponding to the target class.
         */
        fun <R: EnhancedClass> create(factory: EnhancedClassFactory<R>, targetClass: Class<*>, forceRebuild: Boolean = false): R {
            return if (forceRebuild)
                factory.create(targetClass).also {
                    CACHE_MAP[targetClass] = it
                }
            else {
                val existing = CACHE_MAP.computeIfAbsent(targetClass) {
                    factory.create(targetClass)
                }

                @Suppress("UNCHECKED_CAST")
                if (factory.parentEnhancedClass.isAssignableFrom(existing::class.java)) {
                    existing as R
                } else {
                    this.create(factory, targetClass, true)
                }
            }
        }

        /**
         * Pre-caches the [JavaEnhancedClass] instance for the specified target class.
         *
         * Calls [create] to ensure the enhanced class for the target class is cached,
         * avoiding the performance cost of first-time creation during later access.
         *
         * @param targetClass The target class to pre-cache.
         */
        fun precache(factory: EnhancedClassFactory<*>, targetClass: Class<*>) {
            this.create(factory, targetClass)
        }
    }
}