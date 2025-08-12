package com.lovelycatv.vertex.reflect.enhanced

import com.lovelycatv.vertex.reflect.MethodSignature
import com.lovelycatv.vertex.reflect.ReflectUtils
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
        return this.getIndex(MethodSignature(methodName, *parameters))
    }

    fun getIndex(signature: MethodSignature): Int {
        return this.signatureToIndex[signature]
            ?: throw NoSuchMethodException("Method $signature not found in ${originalClass.canonicalName}. Methods: ${this.signatureToIndex.toList()}")
    }

    fun getConstructorIndex(signature: MethodSignature): Int {
        return this.getIndex(signature.copy(name = ReflectUtils.CONSTRUCTOR_NAME))
    }

    fun getConstructorIndex(vararg parameters: Class<*>): Int {
        return this.getIndex(ReflectUtils.CONSTRUCTOR_NAME, *parameters)
    }

    fun getMethod(signature: MethodSignature): EnhancedMethod {
        return EnhancedMethod(this, this.getIndex(signature))
    }

    fun getMethod(methodName: String, vararg parameters: Class<*>): EnhancedMethod {
        return EnhancedMethod(this, this.getIndex(methodName, *parameters))
    }

    fun getConstructor(signature: MethodSignature): EnhancedConstructor {
        return EnhancedConstructor(this, this.getConstructorIndex(signature))
    }

    fun getConstructor(vararg parameters: Class<*>): EnhancedConstructor {
        return EnhancedConstructor(this, this.getConstructorIndex(*parameters))
    }


    abstract fun invokeMethod(target: Any?, index: Int, vararg args: Any?): Any?


    companion object {
        private val CACHE_MAP: MutableMap<Class<*>, EnhancedClass> = mutableMapOf()

        @JvmStatic
        fun createNative(targetClass: Class<*>, forceRebuild: Boolean = false, classLoader: ClassLoader? = null): NativeEnhancedClass {
            return this.create(NativeEnhancedClassFactory(), targetClass, forceRebuild, classLoader)
        }

        @JvmStatic
        fun createJava(targetClass: Class<*>, forceRebuild: Boolean = false, classLoader: ClassLoader? = null): JavaEnhancedClass {
            return this.create(JavaEnhancedClassFactory(), targetClass, forceRebuild, classLoader)
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
        @JvmStatic
        fun <R: EnhancedClass> create(
            factory: EnhancedClassFactory<R>,
            targetClass: Class<*>,
            forceRebuild: Boolean,
            classLoader: ClassLoader?
        ): R {
            return if (forceRebuild)
                factory.create(targetClass, classLoader).also {
                    CACHE_MAP[targetClass] = it
                }
            else {
                val existing = CACHE_MAP.computeIfAbsent(targetClass) {
                    factory.create(targetClass, classLoader)
                }

                @Suppress("UNCHECKED_CAST")
                if (factory.parentEnhancedClass.isAssignableFrom(existing::class.java)) {
                    existing as R
                } else {
                    this.create(factory, targetClass, true, classLoader)
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
        @JvmStatic
        fun precache(factory: EnhancedClassFactory<*>, targetClass: Class<*>, classLoader: ClassLoader? = null) {
            this.create(factory, targetClass, true, classLoader)
        }
    }
}