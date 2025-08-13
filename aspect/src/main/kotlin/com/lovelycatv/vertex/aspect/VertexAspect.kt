package com.lovelycatv.vertex.aspect

import com.lovelycatv.vertex.proxy.AbstractProxyFactory
import com.lovelycatv.vertex.proxy.MethodInterceptor
import com.lovelycatv.vertex.proxy.MethodProxy
import org.aspectj.weaver.tools.PointcutParser
import org.aspectj.weaver.tools.PointcutPrimitive
import java.lang.reflect.Method


/**
 * @author lovelycat
 * @since 2025-08-01 02:04
 * @version 1.0
 */
object VertexAspect {
    private val executionParser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
        setOf(PointcutPrimitive.EXECUTION),
        this::class.java.classLoader
    )

    @JvmStatic
    fun <T: Any> weave(targetClass: Class<T>, originalTarget: T, vararg pointCuts: PointCut): T {
        val factory = AbstractProxyFactory.getVertexFactory(targetClass)

        factory.methodInterceptor(object : MethodInterceptor() {
            override fun intercept(target: Any, method: Method, args: Array<out Any?>, methodProxy: MethodProxy): Any? {
                val allFitPointCuts = pointCuts.filter {
                    executionParser.parsePointcutExpression(it.expression).matchesMethodExecution(method).alwaysMatches()
                }

                return if (allFitPointCuts.isNotEmpty()) {
                    val beforeAspects = allFitPointCuts.flatMap { it.aspects.filterIsInstance<BeforeAspect>() }.sortedBy { it.order }
                    val aroundAspects = allFitPointCuts.flatMap { it.aspects.filterIsInstance<AroundAspect>() }.sortedBy { it.order }
                    val afterThrowingAspects = allFitPointCuts.flatMap { it.aspects.filterIsInstance<AfterThrowingAspect>() }.sortedBy { it.order }
                    val afterAspects = allFitPointCuts.flatMap { it.aspects.filterIsInstance<AfterAspect>() }.sortedBy { it.order }

                    beforeAspects.forEach {
                        it.before(target, method, args)
                    }

                    // Around
                    val result = try {
                        if (aroundAspects.isNotEmpty()) {
                            val chain = AroundAspectChain(originalTarget, method, args, methodProxy, aroundAspects)
                            chain.nextAspect(null)
                            chain.currentResult
                        } else {
                            method.invoke(originalTarget, *args)
                        }
                    } catch (e: Throwable) {
                        if (afterThrowingAspects.isNotEmpty()) {
                            afterThrowingAspects.forEach {
                                it.afterThrowing(target, method, args, e)
                            }
                        } else {
                            throw e
                        }
                    }

                    afterAspects.forEach {
                        it.after(target, method, args, result)
                    }

                    result
                } else {
                    method.invoke(originalTarget, *args)
                }
            }

        })
        return factory.create()
    }
}