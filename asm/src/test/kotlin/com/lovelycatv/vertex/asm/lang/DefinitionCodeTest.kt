package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.VertexASM
import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.asm.toType
import com.lovelycatv.vertex.reflect.noArgsConstructor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * @author lovelycat
 * @since 2025-08-07 13:10
 * @version 1.0
 */
class DefinitionCodeTest {
    @BeforeEach
    fun beforeEach() {
        VertexASMLog.setEnableDebugging(true)
    }

    @Test
    fun throwTest() {
        val testClass = ClassDeclaration.fromExpression("public class InnerTest") {
            this.addMethodFromExpression("public void test()") {
                throwException(TestException::class.java.toType()) {
                    newInstance(TestException::class.java, arrayOf(TypeDeclaration.STRING)) {
                        loadConstant("test message")
                    }
                }
                returnFunc()
            }
        }

        val instanceClazz = VertexASM.loadClassFromDeclaration(testClass)
        val testMethod = instanceClazz.getDeclaredMethod("test")
        val instance = instanceClazz.noArgsConstructor()!!.newInstance()

        try {
            testMethod.invoke(instance)
        } catch (e: Exception) {
            assertEquals(TestException::class.java, e.cause!!::class.java)
        }
    }

    @Test
    fun instanceOfTest() {
        val testClass = ClassDeclaration.fromExpression("public class InnerTest") {
            this.addMethodFromExpression("public boolean test1()") {
                newInstance(TestException::class.java, arrayOf(TypeDeclaration.STRING)) {
                    loadConstant("test message")
                }
                instanceOf(TestException::class.java.toType())
                returnFunc()
            }

            this.addMethodFromExpression("public boolean test2()") {
                newInstance(TestException::class.java, arrayOf(TypeDeclaration.STRING)) {
                    loadConstant("test message")
                }
                instanceOf(String::class.java.toType())
                returnFunc()
            }
        }

        val instanceClazz = VertexASM.loadClassFromDeclaration(testClass)
        val test1Method = instanceClazz.getDeclaredMethod("test1")
        val test2Method = instanceClazz.getDeclaredMethod("test2")
        val instance = instanceClazz.noArgsConstructor()!!.newInstance()

        assertEquals(true, test1Method.invoke(instance))
        assertEquals(false, test2Method.invoke(instance))
    }
}