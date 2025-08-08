package com.lovelycatv.vertex.reflect.proxy

import com.lovelycatv.vertex.reflect.MethodSignature
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Modifier

/**
 * @author lovelycat
 * @since 2025-08-08 08:56
 * @version 1.0
 */
abstract class EnhancedClass(val originalClass: Class<*>) {
    protected val methodHandles: Array<MethodHandle>
    private val signatureToIndex: MutableMap<MethodSignature, Int> = mutableMapOf()

    val methodSize: Int get() = this.methodHandles.size

    init {
        val lookup = MethodHandles.lookup()

        val qualifiedMethods = originalClass.declaredMethods.filter { !Modifier.isFinal(it.modifiers) && !it.isSynthetic }

        methodHandles = qualifiedMethods.mapIndexed { index, method ->
            val methodHandle = lookup.unreflect(method)
            signatureToIndex[MethodSignature(method)] = index
            methodHandle
        }.toTypedArray()
    }

    fun getIndex(methodName: String, vararg parameters: Class<*>): Int {
        val signature = MethodSignature(methodName, *parameters)

        return this.signatureToIndex[signature]
            ?: throw NoSuchMethodException("Method $signature not found in ${originalClass.canonicalName}.")
    }

    abstract fun invokeMethod(target: Any, index: Int, vararg args: Any?): Any?

    companion object {
        private val CACHE_MAP = mutableMapOf<Class<*>, EnhancedClass>()

        /**
         * Creates or retrieves the [EnhancedClass] instance for the given target class.
         *
         * If an instance already exists in the cache, it returns that instance.
         * Otherwise, it creates a new one using [EnhancedClassFactory] and caches it.
         *
         * @param targetClass The target class to enhance.
         * @return The [EnhancedClass] instance corresponding to the target class.
         */
        fun create(targetClass: Class<*>): EnhancedClass {
            return CACHE_MAP.computeIfAbsent(targetClass) {
                EnhancedClassFactory.INSTANCE.create(targetClass)
            }
        }

        /**
         * Pre-caches the [EnhancedClass] instance for the specified target class.
         *
         * Calls [create] to ensure the enhanced class for the target class is cached,
         * avoiding the performance cost of first-time creation during later access.
         *
         * @param targetClass The target class to pre-cache.
         */
        fun precache(targetClass: Class<*>) {
            this.create(targetClass)
        }
    }
}