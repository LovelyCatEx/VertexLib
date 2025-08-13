package com.lovelycatv.vertex.proxy

import com.lovelycatv.vertex.log.logger
import net.sf.cglib.proxy.Enhancer

import org.junit.jupiter.api.Test
import java.lang.reflect.Method

/**
 * @author lovelycat
 * @since 2025-08-12 18:45
 * @version 1.0
 */
class VertexProxyBenchmark {
    private val logger = logger()

    private fun <R> timeAnalysis(preMsg: String, fx: () -> R): R {
        val s = System.nanoTime()
        val r = fx.invoke()
        logger.info("$preMsg: ${1.0 * (System.nanoTime() - s) / 1000000}ms")
        return r
    }

    private val testTimes = 10000000

    private fun runTestCode(fx: () -> Unit) {
        for (i in 0..<testTimes) {
            fx.invoke()
        }
    }

    @Test
    fun cglib() {
        val proxyInstance = timeAnalysis("Cglib-new") {
            val enhancer = Enhancer()
            enhancer.setSuperclass(TargetClass::class.java)
            enhancer.setCallback(object : net.sf.cglib.proxy.MethodInterceptor {
                override fun intercept(obj: Any, method: Method, args: Array<out Any>, proxy:  net.sf.cglib.proxy.MethodProxy): Any {
                    return proxy.invokeSuper(obj, args)
                }
            })

            enhancer.create() as TargetClass
            enhancer.create() as TargetClass
            enhancer.create() as TargetClass
        }

        timeAnalysis("Cglib-invoke") {
            val arr = IntArray(3) { 0 }
            runTestCode {
                proxyInstance.add(1, 1)
                proxyInstance.getIntArrayLength(arr)
            }
        }
    }

    @Test
    fun vertex() {
        val proxyInstance = timeAnalysis("Vertex-new") {
            val factory = VertexProxyFactory(TargetClass::class.java).methodInterceptor(object : MethodInterceptor() {
                override fun intercept(
                    target: Any,
                    method: Method,
                    args: Array<out Any?>,
                    methodProxy: MethodProxy
                ): Any? {
                    return methodProxy.invoke(target, *args)
                }
            })

            factory.create()
            factory.create()
            factory.create()
        }

        timeAnalysis("Vertex-invoke") {
            val arr = IntArray(3) { 0 }
            runTestCode {
                proxyInstance.add(1, 1)
                proxyInstance.getIntArrayLength(arr)
            }
        }
    }
}