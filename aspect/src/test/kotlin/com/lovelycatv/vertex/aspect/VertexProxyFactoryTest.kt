package com.lovelycatv.vertex.aspect

import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.aspect.proxy.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.Method
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VertexProxyFactoryTest {
    private lateinit var factory: VertexProxyFactory<TargetClass>

    @BeforeEach
    fun beforeEach() {
        // EnhancedClassFactory.setEnableDebugging(true)
        // AbstractProxyFactory.setEnabledDebugging(true)
        VertexASMLog.setEnableDebugging(true)

        factory = VertexProxyFactory(TargetClass::class.java)
    }

    @Test
    fun createWithNoArgsConstructor() {
        val proxyInstance = factory.create()
        assertEquals(3, proxyInstance.getIntArrayLength(IntArray(3) { 0 }))
    }

    @Test
    fun create() {
        val proxyInstance = factory.create(arrayOf(String::class.java, IntArray::class.java), "", IntArray(0))
        assertEquals(3, proxyInstance.getIntArrayLength(IntArray(3) { 0 }))
    }

    @Test
    fun createWithPolicy() {
        factory.methodProxyPolicy {
            if (it.name == "sayHello") {
                MethodProxyPolicy.NO_OPERATION
            } else {
                MethodProxyPolicy.INTERCEPT
            }
        }

        val proxyInstance = factory.create(arrayOf(String::class.java, IntArray::class.java), "", IntArray(0))

        assertTrue(proxyInstance::class.java.declaredMethods.find { it.name == "sayHello" } == null)
    }

    @Test
    fun createWithInterceptor() {
        factory.methodInterceptor(object : MethodInterceptor() {
            override fun intercept(
                target: Any,
                method: Method,
                args: Array<out Any?>,
                methodProxy: MethodProxy
            ): Any? {
                println("before")
                val r = methodProxy.invoke(target, *args)
                println("after")
                return if (method.name == "add") (r as Int) + 233 else r
            }
        })

        val proxyInstance = factory.create(arrayOf(String::class.java, IntArray::class.java), "", IntArray(0))

        assertEquals(235, proxyInstance.add(1, 1))
    }

    @Test
    fun createWithCustomName() {
        factory.naming { "${it.simpleName}ByCustomName" }

        val proxyInstance = factory.create()

        assertEquals("${TargetClass::class.java.simpleName}ByCustomName", proxyInstance::class.java.simpleName)
    }
}