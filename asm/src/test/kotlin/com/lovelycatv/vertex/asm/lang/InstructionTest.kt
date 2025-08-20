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
}