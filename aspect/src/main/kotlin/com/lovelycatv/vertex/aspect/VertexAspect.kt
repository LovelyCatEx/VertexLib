package com.lovelycatv.vertex.aspect

import java.lang.reflect.Proxy


/**
 * @author lovelycat
 * @since 2025-08-01 02:04
 * @version 1.0
 */
object VertexAspect {
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> createJavaProxy(target: T, aspect: AbstractAspect): T {
        val clazz: Class<*> = target::class.java
        val loader = clazz.classLoader
        val interfaces = clazz.interfaces

        require(interfaces.isNotEmpty()) { "Object must implement at least one interface" }

        return Proxy.newProxyInstance(
            loader,
            interfaces
        ) { proxy, method, args ->
            aspect.before(target, method, args ?: emptyArray())
            val result = aspect.invocation(target, method, args ?: arrayOf())
            aspect.after(target, method, args ?: emptyArray())
            result
        } as T
    }
}