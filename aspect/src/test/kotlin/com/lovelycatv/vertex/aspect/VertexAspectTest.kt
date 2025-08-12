package com.lovelycatv.vertex.aspect

import com.lovelycatv.vertex.aspect.proxy.MethodProxy
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.reflect.Method

class VertexAspectTest {

    @Test
    fun weave() {
        val target = TargetClass()
        val weaved = VertexAspect.weave(
            TargetClass::class.java,
            target,
            PointCut("execution(* com.lovelycatv.vertex.aspect.TargetClass.add(int, .., int))").apply {
                addAspect(object : BeforeAspect(0) {
                    override fun before(target: Any, method: Method, args: Array<out Any?>) {
                        println("before 0")
                    }
                })

                addAspect(object : BeforeAspect(2) {
                    override fun before(target: Any, method: Method, args: Array<out Any?>) {
                        println("before 2")
                    }
                })

                addAspect(object : BeforeAspect(1) {
                    override fun before(target: Any, method: Method, args: Array<out Any?>) {
                        println("before 1")
                    }
                })

                addAspect(object : AfterAspect(0) {
                    override fun after(target: Any, method: Method, args: Array<out Any?>, returns: Any?) {
                        println("after")
                    }
                })

                addAspect(object : AroundAspect(0) {
                    override fun around(
                        target: Any,
                        method: Method,
                        args: Array<out Any?>,
                        methodProxy: MethodProxy,
                        procedure: AroundAspectChain
                    ) {
                        println("Around 0 Before")
                        procedure.nextAspect(method.invoke(target, *args))
                        println("Around 0 After")
                    }
                })

                addAspect(object : AroundAspect(1) {
                    override fun around(
                        target: Any,
                        method: Method,
                        args: Array<out Any?>,
                        methodProxy: MethodProxy,
                        procedure: AroundAspectChain
                    ) {
                        println("Around 1 Before")
                        println("Last: ${procedure.currentResult}")
                        procedure.nextAspect(procedure.currentResult as Int + 233)
                        println("Around 1 After")
                    }
                })

                addAspect(object : AroundAspect(2) {
                    override fun around(
                        target: Any,
                        method: Method,
                        args: Array<out Any?>,
                        methodProxy: MethodProxy,
                        procedure: AroundAspectChain
                    ) {
                        println("Around 2 Before")
                        println("Last: ${procedure.currentResult}")
                        procedure.nextAspect()
                        println("Around 2 After")
                    }
                })
            }
        )

        assertEquals(235, weaved.add(1, 1))
    }

    @Test
    fun weaveWithoutAround() {
        val target = TargetClass()
        val weaved = VertexAspect.weave(
            TargetClass::class.java,
            target,
            PointCut("execution(* com.lovelycatv.vertex.aspect.TargetClass.add(int, .., int))").apply {
                addAspect(object : BeforeAspect(0) {
                    override fun before(target: Any, method: Method, args: Array<out Any?>) {
                        println("before 0")
                    }
                })

                addAspect(object : BeforeAspect(2) {
                    override fun before(target: Any, method: Method, args: Array<out Any?>) {
                        println("before 2")
                    }
                })

                addAspect(object : BeforeAspect(1) {
                    override fun before(target: Any, method: Method, args: Array<out Any?>) {
                        println("before 1")
                    }
                })

                addAspect(object : AfterAspect(0) {
                    override fun after(target: Any, method: Method, args: Array<out Any?>, returns: Any?) {
                        println("after")
                    }
                })
            }
        )

        assertEquals(2, weaved.add(1, 1))
    }

    @Test
    fun weaveWithException() {
        val target = TargetClass()
        val weaved = VertexAspect.weave(
            TargetClass::class.java,
            target,
            PointCut("execution(* com.lovelycatv.vertex.aspect.TargetClass.*(..))").apply {
                addAspect(object : AfterThrowingAspect(0) {
                    override fun afterThrowing(
                        target: Any,
                        method: Method,
                        args: Array<out Any?>,
                        throwable: Throwable
                    ) {
                        println("Exception: ${throwable.cause}, ${throwable.message}")
                    }
                })
            }
        )

        weaved.throwing()
    }

    @Test
    fun nestedWeave() {
        val target = TargetClass()
        val weaved1 = VertexAspect.weave(
            TargetClass::class.java,
            target,
            PointCut("execution(* com.lovelycatv.vertex.aspect.TargetClass.add(int, .., int))").apply {
                addAspect(object : BeforeAspect(0) {
                    override fun before(target: Any, method: Method, args: Array<out Any?>) {
                        println("before 0")
                    }
                })

                addAspect(object : BeforeAspect(2) {
                    override fun before(target: Any, method: Method, args: Array<out Any?>) {
                        println("before 2")
                    }
                })

                addAspect(object : BeforeAspect(1) {
                    override fun before(target: Any, method: Method, args: Array<out Any?>) {
                        println("before 1")
                    }
                })

                addAspect(object : AfterAspect(3) {
                    override fun after(target: Any, method: Method, args: Array<out Any?>, returns: Any?) {
                        println("after 3")
                    }
                })
            },
            PointCut("execution(* com.lovelycatv.vertex.aspect.TargetClass.*(..))").apply {
                addAspect(object : AfterAspect(0) {
                    override fun after(target: Any, method: Method, args: Array<out Any?>, returns: Any?) {
                        println("after 2")
                    }
                })
            }
        )

        weaved1.add(1, 1)
    }
}