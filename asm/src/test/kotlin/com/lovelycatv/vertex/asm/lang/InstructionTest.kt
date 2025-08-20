package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.VertexASM
import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.reflect.noArgsConstructor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * @author lovelycat
 * @since 2025-08-20 14:31
 * @version 1.0
 */
class InstructionTest {
    @BeforeEach
    fun beforeEach() {
        VertexASMLog.setEnableDebugging(true)
    }

    @Test
    fun swap() {
        val classDeclaration = ClassDeclaration.fromExpression("public class SwapTest") {
            "public final int before(int a)".toMethod {
                loadVariable("a")
                loadConstant(233)
                returnFunc()
            }

            "public final int after(int a)".toMethod {
                loadVariable("a")
                loadConstant(233)
                swap()
                returnFunc()
            }
        }

        val clazz = VertexASM.loadClassFromDeclaration(classDeclaration)
        val instance = clazz.noArgsConstructor()!!.newInstance()
        val methodBefore = clazz.getMethod("before", Int::class.java)
        val methodAfter = clazz.getMethod("after", Int::class.java)

        assertEquals(233, methodBefore.invoke(instance, 123))
        assertEquals(123, methodAfter.invoke(instance, 123))
    }

    @Test
    fun dup() {
        val classDeclaration = ClassDeclaration.fromExpression("public class DuplicateTest") {
            "public final int before()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                pop()
                returnFunc()
            }

            "public final int after()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                dup()
                pop()
                returnFunc()
            }
        }

        val clazz = VertexASM.loadClassFromDeclaration(classDeclaration)
        val instance = clazz.noArgsConstructor()!!.newInstance()
        val methodBefore = clazz.getMethod("before")
        val methodAfter = clazz.getMethod("after")

        assertEquals(2, methodBefore.invoke(instance))
        assertEquals(3, methodAfter.invoke(instance))
    }

    @Test
    fun dup_x1() {
        val classDeclaration = ClassDeclaration.fromExpression("public class DuplicateTest") {
            "public final int before()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                pop()
                returnFunc()
            }

            "public final int after()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                dup_x1()
                pop()
                returnFunc()
            }
        }

        val clazz = VertexASM.loadClassFromDeclaration(classDeclaration)
        val instance = clazz.noArgsConstructor()!!.newInstance()
        val methodBefore = clazz.getMethod("before")
        val methodAfter = clazz.getMethod("after")

        assertEquals(2, methodBefore.invoke(instance))
        assertEquals(2, methodAfter.invoke(instance))
    }

    @Test
    fun dup_x2() {
        val classDeclaration = ClassDeclaration.fromExpression("public class DuplicateTest") {
            "public final int before()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                pop()
                returnFunc()
            }

            "public final int after()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                dup_x2()
                pop(2)
                returnFunc()
            }
        }

        val clazz = VertexASM.loadClassFromDeclaration(classDeclaration)
        val instance = clazz.noArgsConstructor()!!.newInstance()
        val methodBefore = clazz.getMethod("before")
        val methodAfter = clazz.getMethod("after")

        assertEquals(2, methodBefore.invoke(instance))
        assertEquals(1, methodAfter.invoke(instance))
    }

    @Test
    fun dup2() {
        val classDeclaration = ClassDeclaration.fromExpression("public class DuplicateTest") {
            "public final int before()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                pop()
                returnFunc()
            }

            "public final int after()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                dup2()
                pop(2)
                returnFunc()
            }
        }

        val clazz = VertexASM.loadClassFromDeclaration(classDeclaration)
        val instance = clazz.noArgsConstructor()!!.newInstance()
        val methodBefore = clazz.getMethod("before")
        val methodAfter = clazz.getMethod("after")

        assertEquals(2, methodBefore.invoke(instance))
        assertEquals(3, methodAfter.invoke(instance))
    }

    @Test
    fun dup2_x1() {
        val classDeclaration = ClassDeclaration.fromExpression("public class DuplicateTest") {
            "public final int before()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                pop()
                returnFunc()
            }

            "public final int after()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                loadConstant(1L)
                dup2_x1()
                pop(2)
                returnFunc()
            }
        }

        val clazz = VertexASM.loadClassFromDeclaration(classDeclaration)
        val instance = clazz.noArgsConstructor()!!.newInstance()
        val methodBefore = clazz.getMethod("before")
        val methodAfter = clazz.getMethod("after")

        assertEquals(2, methodBefore.invoke(instance))
        assertEquals(3, methodAfter.invoke(instance))
    }

    @Test
    fun dup2_x2() {
        val classDeclaration = ClassDeclaration.fromExpression("public class DuplicateTest") {
            "public final int before()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                pop()
                returnFunc()
            }

            "public final long after()".toMethod {
                loadConstant(1)
                loadConstant(2)
                loadConstant(3)
                loadConstant(1L)
                dup2_x2()
                pop(4)
                returnFunc()
            }
        }

        val clazz = VertexASM.loadClassFromDeclaration(classDeclaration)
        val instance = clazz.noArgsConstructor()!!.newInstance()
        val methodBefore = clazz.getMethod("before")
        val methodAfter = clazz.getMethod("after")

        assertEquals(2, methodBefore.invoke(instance))
        assertEquals(1L, methodAfter.invoke(instance))
    }

    @Test
    fun longCompare() {
        val classDeclaration = ClassDeclaration.fromExpression("public class LongCompareTest") {
            "public final int compare1()".toMethod {
                loadConstant(3L)
                loadConstant(6L)
                longCompare()
                returnFunc()
            }

            "public final int compare2()".toMethod {
                loadConstant(3L)
                loadConstant(3L)
                longCompare()
                returnFunc()
            }

            "public final int compare3()".toMethod {
                loadConstant(3L)
                loadConstant(2L)
                longCompare()
                returnFunc()
            }
        }

        val clazz = VertexASM.loadClassFromDeclaration(classDeclaration)
        val instance = clazz.noArgsConstructor()!!.newInstance()
        val method1 = clazz.getMethod("compare1")
        val method2 = clazz.getMethod("compare2")
        val method3 = clazz.getMethod("compare3")

        assertEquals(-1, method1.invoke(instance))
        assertEquals(0, method2.invoke(instance))
        assertEquals(1, method3.invoke(instance))
    }

    @Test
    fun floatCompare() {
        val classDeclaration = ClassDeclaration.fromExpression("public class FloatCompareTest") {
            "public final int compare1()".toMethod {
                loadConstant(3f)
                loadConstant(6f)
                floatCompare(true)
                returnFunc()
            }

            "public final int compare2()".toMethod {
                loadConstant(3f)
                loadConstant(3f)
                floatCompare(true)
                returnFunc()
            }

            "public final int compare3()".toMethod {
                loadConstant(3f)
                loadConstant(2f)
                floatCompare(true)
                returnFunc()
            }

            "public final int compare4()".toMethod {
                loadConstant(3f)
                loadConstant(Float.NaN)
                floatCompare(true)
                returnFunc()
            }

            "public final int compare5()".toMethod {
                loadConstant(3f)
                loadConstant(Float.NaN)
                floatCompare(false)
                returnFunc()
            }
        }

        val clazz = VertexASM.loadClassFromDeclaration(classDeclaration)
        val instance = clazz.noArgsConstructor()!!.newInstance()
        val method1 = clazz.getMethod("compare1")
        val method2 = clazz.getMethod("compare2")
        val method3 = clazz.getMethod("compare3")
        val method4 = clazz.getMethod("compare4")
        val method5 = clazz.getMethod("compare5")

        assertEquals(-1, method1.invoke(instance))
        assertEquals(0, method2.invoke(instance))
        assertEquals(1, method3.invoke(instance))
        assertEquals(1, method4.invoke(instance))
        assertEquals(-1, method5.invoke(instance))
    }

    @Test
    fun doubleCompare() {
        val classDeclaration = ClassDeclaration.fromExpression("public class DoubleCompareTest") {
            "public final int compare1()".toMethod {
                loadConstant(3.0)
                loadConstant(6.0)
                doubleCompare(true)
                returnFunc()
            }

            "public final int compare2()".toMethod {
                loadConstant(3.0)
                loadConstant(3.0)
                doubleCompare(true)
                returnFunc()
            }

            "public final int compare3()".toMethod {
                loadConstant(3.0)
                loadConstant(2.0)
                doubleCompare(true)
                returnFunc()
            }

            "public final int compare4()".toMethod {
                loadConstant(3.0)
                loadConstant(Double.NaN)
                doubleCompare(true)
                returnFunc()
            }

            "public final int compare5()".toMethod {
                loadConstant(3.0)
                loadConstant(Double.NaN)
                doubleCompare(false)
                returnFunc()
            }
        }

        val clazz = VertexASM.loadClassFromDeclaration(classDeclaration)
        val instance = clazz.noArgsConstructor()!!.newInstance()
        val method1 = clazz.getMethod("compare1")
        val method2 = clazz.getMethod("compare2")
        val method3 = clazz.getMethod("compare3")
        val method4 = clazz.getMethod("compare4")
        val method5 = clazz.getMethod("compare5")

        assertEquals(-1, method1.invoke(instance))
        assertEquals(0, method2.invoke(instance))
        assertEquals(1, method3.invoke(instance))
        assertEquals(1, method4.invoke(instance))
        assertEquals(-1, method5.invoke(instance))
    }
}