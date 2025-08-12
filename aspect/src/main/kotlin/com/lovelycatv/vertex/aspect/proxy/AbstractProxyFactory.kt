package com.lovelycatv.vertex.aspect.proxy

import com.lovelycatv.vertex.util.StringUtils
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * @author lovelycat
 * @since 2025-08-12 17:27
 * @version 1.0
 */
abstract class AbstractProxyFactory<T: I, I>(protected val superClass: Class<T>) {
    private var proxyNaming = ProxyNaming {
        "${it.simpleName}\$\$ByVertex\$\$${StringUtils.randomHex(8)}"
    }

    protected var methodInterceptor: MethodInterceptor = object : MethodInterceptor() {
        override fun intercept(target: Any, method: Method, args: Array<out Any?>, methodProxy: MethodProxy): Any? {
            return methodProxy.invoke(target, *args)
        }
    }

    protected var methodProxyPolicyProvider = MethodProxyPolicyProvider { MethodProxyPolicy.INTERCEPT }

    init {
        if (Modifier.isFinal(this.superClass.modifiers)) {
            throw UnsupportedOperationException("Cannot inherit from final class ${this.superClass.canonicalName}.")
        }
    }

    open fun naming(proxyNaming: ProxyNaming) = this.apply {
        this.proxyNaming = proxyNaming
    }

    fun methodInterceptor(interceptor: MethodInterceptor) = this.apply {
        this.methodInterceptor = interceptor
    }

    fun methodProxyPolicy(provider: MethodProxyPolicyProvider) = this.apply {
        this.methodProxyPolicyProvider = provider
    }

    fun create() = this.create(arrayOf())

    open fun create(constructorParameterTypes: Array<out Class<*>>, vararg constructorArgs: Any?): I {
        return this.internalCreate(
            proxyClassName = this.proxyNaming.getClassName(this.superClass),
            constructorParameterTypes,
            *constructorArgs
        )
    }

    abstract fun internalCreate(
        proxyClassName: String,
        constructorParameterTypes: Array<out Class<*>>,
        vararg constructorArgs: Any?
    ): I

    fun getHashCode(): Int {
        var hashCode = 17
        hashCode += 31 * hashCode + (this.superClass.hashCode())
        hashCode += 31 * hashCode + (this.proxyNaming.hashCode())
        hashCode += 31 * hashCode + (this.methodInterceptor.hashCode())
        hashCode += 31 * hashCode + (this.methodProxyPolicyProvider.hashCode())
        return hashCode
    }


    companion object {
        private val DEBUGGING = ThreadLocal<Boolean>().apply { set(false) }

        fun setEnabledDebugging(enabled: Boolean) {
            DEBUGGING.set(enabled)
        }

        fun isDebuggingEnabled() = DEBUGGING.get()

        @JvmStatic
        fun <T: I, I> getJavaFactory(targetClass: Class<T>): JavaProxyFactory<T, I> {
            return JavaProxyFactory(targetClass)
        }

        @JvmStatic
        fun <T> getVertexFactory(targetClass: Class<T>): VertexProxyFactory<T> {
            return VertexProxyFactory(targetClass)
        }
    }
}