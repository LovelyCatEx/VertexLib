package com.lovelycatv.vertex.proxy

import com.lovelycatv.vertex.reflect.MethodSignature
import com.lovelycatv.vertex.proxy.enhanced.EnhancedClass
import java.lang.reflect.Proxy

/**
 * @author lovelycat
 * @since 2025-08-12 17:34
 * @version 1.0
 */
class JavaProxyFactory<T: I, I>(superClass: Class<T>) : AbstractProxyFactory<T, I>(superClass) {
    override fun naming(proxyNaming: ProxyNaming): AbstractProxyFactory<T, I> {
        throw UnsupportedOperationException("Custom proxy class name is unsupported in Java Proxy.")
    }

    @Suppress("UNCHECKED_CAST")
    override fun internalCreate(proxyClassName: String, constructorParameterTypes: Array<out Class<*>>, vararg constructorArgs: Any?): I {
        val targetClass: Class<*> = super.superClass
        val loader = targetClass.classLoader
        val interfaces = targetClass.interfaces

        require(interfaces.isNotEmpty()) { "Object must implement at least one interface" }

        val enhancedTarget = EnhancedClass.createNative(targetClass, false, loader)
        val target = enhancedTarget
            .getConstructor(*constructorParameterTypes)
            .invokeMethod(*constructorArgs) as Any

        return Proxy.newProxyInstance(
            loader,
            interfaces
        ) { _, method, args ->
            if (super.methodProxyPolicyProvider.getPolicy(method) != MethodProxyPolicy.NO_OPERATION) {
                super.methodInterceptor.intercept(target, method, args, MethodProxy.getProxy(enhancedTarget, MethodSignature(method)))
            } else {
                method.invoke(target, *args)
            }
        } as T
    }

}