package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.JavaModifier
import com.lovelycatv.vertex.asm.VertexASM
import com.lovelycatv.vertex.asm.VertexASMLog
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * @author lovelycat
 * @since 2025-08-07 13:10
 * @version 1.0
 */
class CalculationCodeTest {
    @BeforeEach
    fun beforeEach() {
        VertexASMLog.setEnableDebugging(true)
    }

    @Test
    fun add() {
        val className = "AddTest"
        val intAddMethodName = "intAdd"
        val longAddMethodName = "longAdd"
        val floatAddMethodName = "floatAdd"
        val doubleAddMethodName = "doubleAdd"
        val addTestClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intAdd = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intAddMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            intAdd()
            returnFunc()
        }

        val longAdd = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longAddMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG),
                ParameterDeclaration.fromType("b", TypeDeclaration.LONG)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            longAdd()
            returnFunc()
        }

        val floatAdd = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = floatAddMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.FLOAT),
                ParameterDeclaration.fromType("b", TypeDeclaration.FLOAT)
            ),
            returnType = TypeDeclaration.FLOAT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            floatAdd()
            returnFunc()
        }

        val doubleAdd = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = doubleAddMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.DOUBLE),
                ParameterDeclaration.fromType("b", TypeDeclaration.DOUBLE)
            ),
            returnType = TypeDeclaration.DOUBLE,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            doubleAdd()
            returnFunc()
        }

        addTestClass.addMethod(intAdd)
        addTestClass.addMethod(longAdd)
        addTestClass.addMethod(floatAdd)
        addTestClass.addMethod(doubleAdd)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(addTestClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intAddMethodName }?.invoke(instance, 1, 2)
        assertEquals(3, r1)

        val r2 = clazz.declaredMethods.find { it.name == longAddMethodName }?.invoke(instance, 1L, 3L)
        assertEquals(4L, r2)

        val r3 = clazz.declaredMethods.find { it.name == floatAddMethodName }?.invoke(instance, 1f, 2.5f)
        assertEquals(1f + 2.5f, r3)

        val r4 = clazz.declaredMethods.find { it.name == doubleAddMethodName }?.invoke(instance, 1.0, 3.14)
        assertEquals(1.0 + 3.14, r4)
    }
}