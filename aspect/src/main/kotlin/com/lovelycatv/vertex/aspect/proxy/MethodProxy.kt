package com.lovelycatv.vertex.aspect.proxy

import com.lovelycatv.vertex.reflect.MethodSignature
import com.lovelycatv.vertex.reflect.enhanced.EnhancedClass
import com.lovelycatv.vertex.reflect.enhanced.EnhancedMethod
import java.lang.reflect.Method

/**
 * @author lovelycat
 * @since 2025-08-11 03:57
 * @version 1.0
 */
class MethodProxy(enhancedClass: EnhancedClass, signature: MethodSignature) {
    private val targetMethod = enhancedClass.getMethod(signature)

    constructor(proxyClass: Class<*>, signature: MethodSignature) : this(
        EnhancedClass.createNative(proxyClass, false, proxyClass.classLoader),
        signature
    )

    fun invoke(target: Any, vararg args: Any?): Any? {
        return this.targetMethod.invokeMethod(target, *args)
    }

    companion object {
        private val METHOD_CACHE_MAP = mutableMapOf<Class<*>, MutableMap<MethodSignature, Method>>()

        @JvmStatic
        fun getMethod(clazz: Class<*>, signature: MethodSignature): Method {
            return METHOD_CACHE_MAP.computeIfAbsent(clazz) {
                mutableMapOf()
            }.computeIfAbsent(signature) {
                clazz.declaredMethods.find { MethodSignature(it) == signature }
                    ?: throw NoSuchMethodException("Method $signature not found in ${clazz.simpleName} / ${clazz.canonicalName}")
            }
        }

        @JvmStatic
        fun getProxy(clazz: Class<*>, signature: MethodSignature): MethodProxy {
            return MethodProxy(clazz, signature)
        }

        @JvmStatic
        fun getProxy(enhancedClass: EnhancedClass, signature: MethodSignature): MethodProxy {
            return MethodProxy(enhancedClass, signature)
        }
    }
}