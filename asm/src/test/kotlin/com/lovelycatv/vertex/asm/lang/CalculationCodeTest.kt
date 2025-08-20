package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.VertexASM
import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.reflect.JavaModifier
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

    @Test
    fun sub() {
        val className = "SubTest"
        val intSubMethodName = "intSub"
        val longSubMethodName = "longSub"
        val floatSubMethodName = "floatSub"
        val doubleSubMethodName = "doubleSub"
        val subTestClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intSub = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intSubMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            intSubtract()
            returnFunc()
        }

        val longSub = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longSubMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG),
                ParameterDeclaration.fromType("b", TypeDeclaration.LONG)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            longSubtract()
            returnFunc()
        }

        val floatSub = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = floatSubMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.FLOAT),
                ParameterDeclaration.fromType("b", TypeDeclaration.FLOAT)
            ),
            returnType = TypeDeclaration.FLOAT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            floatSubtract()
            returnFunc()
        }

        val doubleSub = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = doubleSubMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.DOUBLE),
                ParameterDeclaration.fromType("b", TypeDeclaration.DOUBLE)
            ),
            returnType = TypeDeclaration.DOUBLE,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            doubleSubtract()
            returnFunc()
        }

        subTestClass.addMethod(intSub)
        subTestClass.addMethod(longSub)
        subTestClass.addMethod(floatSub)
        subTestClass.addMethod(doubleSub)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(subTestClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intSubMethodName }?.invoke(instance, 1, 2)
        assertEquals(-1, r1)

        val r2 = clazz.declaredMethods.find { it.name == longSubMethodName }?.invoke(instance, 1L, 3L)
        assertEquals(-2L, r2)

        val r3 = clazz.declaredMethods.find { it.name == floatSubMethodName }?.invoke(instance, 1f, 2.5f)
        assertEquals(1f - 2.5f, r3)

        val r4 = clazz.declaredMethods.find { it.name == doubleSubMethodName }?.invoke(instance, 1.0, 3.14)
        assertEquals(1.0 - 3.14, r4)
    }

    @Test
    fun mul() {
        val className = "MulTest"
        val intMulMethodName = "intMul"
        val longMulMethodName = "longMul"
        val floatMulMethodName = "floatMul"
        val doubleMulMethodName = "doubleMul"
        val mulTestClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intMul = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intMulMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            intMultiply()
            returnFunc()
        }

        val longMul = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longMulMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG),
                ParameterDeclaration.fromType("b", TypeDeclaration.LONG)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            longMultiply()
            returnFunc()
        }

        val floatMul = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = floatMulMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.FLOAT),
                ParameterDeclaration.fromType("b", TypeDeclaration.FLOAT)
            ),
            returnType = TypeDeclaration.FLOAT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            floatMultiply()
            returnFunc()
        }

        val doubleMul = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = doubleMulMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.DOUBLE),
                ParameterDeclaration.fromType("b", TypeDeclaration.DOUBLE)
            ),
            returnType = TypeDeclaration.DOUBLE,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            doubleMultiply()
            returnFunc()
        }

        mulTestClass.addMethod(intMul)
        mulTestClass.addMethod(longMul)
        mulTestClass.addMethod(floatMul)
        mulTestClass.addMethod(doubleMul)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(mulTestClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intMulMethodName }?.invoke(instance, 1, 2)
        assertEquals(2, r1)

        val r2 = clazz.declaredMethods.find { it.name == longMulMethodName }?.invoke(instance, 1L, 3L)
        assertEquals(3L, r2)

        val r3 = clazz.declaredMethods.find { it.name == floatMulMethodName }?.invoke(instance, 1f, 2.5f)
        assertEquals(1f * 2.5f, r3)

        val r4 = clazz.declaredMethods.find { it.name == doubleMulMethodName }?.invoke(instance, 1.0, 3.14)
        assertEquals(1.0 * 3.14, r4)
    }

    @Test
    fun div() {
        val className = "DivTest"
        val intDivMethodName = "intDiv"
        val longDivMethodName = "longDiv"
        val floatDivMethodName = "floatDiv"
        val doubleDivMethodName = "doubleDiv"
        val divTestClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intDiv = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intDivMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            intDivide()
            returnFunc()
        }

        val longDiv = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longDivMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG),
                ParameterDeclaration.fromType("b", TypeDeclaration.LONG)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            longDivide()
            returnFunc()
        }

        val floatDiv = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = floatDivMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.FLOAT),
                ParameterDeclaration.fromType("b", TypeDeclaration.FLOAT)
            ),
            returnType = TypeDeclaration.FLOAT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            floatDivide()
            returnFunc()
        }

        val doubleDiv = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = doubleDivMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.DOUBLE),
                ParameterDeclaration.fromType("b", TypeDeclaration.DOUBLE)
            ),
            returnType = TypeDeclaration.DOUBLE,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            doubleDivide()
            returnFunc()
        }

        divTestClass.addMethod(intDiv)
        divTestClass.addMethod(longDiv)
        divTestClass.addMethod(floatDiv)
        divTestClass.addMethod(doubleDiv)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(divTestClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intDivMethodName }?.invoke(instance, 6, 2)
        assertEquals(3, r1)

        val r2 = clazz.declaredMethods.find { it.name == longDivMethodName }?.invoke(instance, 10L, 2L)
        assertEquals(5L, r2)

        val r3 = clazz.declaredMethods.find { it.name == floatDivMethodName }?.invoke(instance, 5f, 2f)
        assertEquals(5f / 2f, r3)

        val r4 = clazz.declaredMethods.find { it.name == doubleDivMethodName }?.invoke(instance, 10.0, 4.0)
        assertEquals(10.0 / 4.0, r4)
    }

    @Test
    fun rem() {
        val className = "RemTest"
        val intRemMethodName = "intRem"
        val longRemMethodName = "longRem"
        val floatRemMethodName = "floatRem"
        val doubleRemMethodName = "doubleRem"
        val remTestClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intRem = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intRemMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            intRem()
            returnFunc()
        }

        val longRem = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longRemMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG),
                ParameterDeclaration.fromType("b", TypeDeclaration.LONG)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            longRem()
            returnFunc()
        }

        val floatRem = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = floatRemMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.FLOAT),
                ParameterDeclaration.fromType("b", TypeDeclaration.FLOAT)
            ),
            returnType = TypeDeclaration.FLOAT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            floatRem()
            returnFunc()
        }

        val doubleRem = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = doubleRemMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.DOUBLE),
                ParameterDeclaration.fromType("b", TypeDeclaration.DOUBLE)
            ),
            returnType = TypeDeclaration.DOUBLE,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            doubleRem()
            returnFunc()
        }

        remTestClass.addMethod(intRem)
        remTestClass.addMethod(longRem)
        remTestClass.addMethod(floatRem)
        remTestClass.addMethod(doubleRem)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(remTestClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intRemMethodName }?.invoke(instance, 7, 3)
        assertEquals(7 % 3, r1) // 7 % 3 = 1

        val r2 = clazz.declaredMethods.find { it.name == longRemMethodName }?.invoke(instance, 10L, 4L)
        assertEquals(10L % 4L, r2) // 10L % 4L = 2L

        val r3 = clazz.declaredMethods.find { it.name == floatRemMethodName }?.invoke(instance, 5.5f, 2.0f)
        assertEquals(5.5f % 2.0f, r3) // 5.5f % 2.0f = 1.5f

        val r4 = clazz.declaredMethods.find { it.name == doubleRemMethodName }?.invoke(instance, 10.5, 3.0)
        assertEquals(10.5 % 3.0, r4) // 10.5 % 3.0 = 1.5
    }

    @Test
    fun neg() {
        val className = "NegTest"
        val intNegMethodName = "intNeg"
        val longNegMethodName = "longNeg"
        val floatNegMethodName = "floatNeg"
        val doubleNegMethodName = "doubleNeg"
        val negTestClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intNeg = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intNegMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            intNegative()
            returnFunc()
        }

        val longNeg = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longNegMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            longNegative()
            returnFunc()
        }

        val floatNeg = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = floatNegMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.FLOAT)
            ),
            returnType = TypeDeclaration.FLOAT,
            throws = null
        ) {
            loadMethodParameter(0)
            floatNegative()
            returnFunc()
        }

        val doubleNeg = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = doubleNegMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.DOUBLE)
            ),
            returnType = TypeDeclaration.DOUBLE,
            throws = null
        ) {
            loadMethodParameter(0)
            doubleNegative()
            returnFunc()
        }

        negTestClass.addMethod(intNeg)
        negTestClass.addMethod(longNeg)
        negTestClass.addMethod(floatNeg)
        negTestClass.addMethod(doubleNeg)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(negTestClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intNegMethodName }?.invoke(instance, 5)
        assertEquals(-5, r1)

        val r2 = clazz.declaredMethods.find { it.name == longNegMethodName }?.invoke(instance, 10L)
        assertEquals(-10L, r2)

        val r3 = clazz.declaredMethods.find { it.name == floatNegMethodName }?.invoke(instance, 3.14f)
        assertEquals(-3.14f, r3)

        val r4 = clazz.declaredMethods.find { it.name == doubleNegMethodName }?.invoke(instance, 2.71828)
        assertEquals(-2.71828, r4)
    }

    @Test
    fun shl() {
        val className = "ShlTest"
        val intMethodName = "intShl"
        val longMethodName = "longShl"
        val testClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intShl = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            intShl()
            returnFunc()
        }

        val longShl = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            longShl()
            returnFunc()
        }

        testClass.addMethod(intShl)
        testClass.addMethod(longShl)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(testClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intMethodName }?.invoke(instance, 5, 1)
        assertEquals(10, r1)

        val r2 = clazz.declaredMethods.find { it.name == longMethodName }?.invoke(instance, 10L, 1)
        assertEquals(20L, r2)
    }

    @Test
    fun shr() {
        val className = "ShrTest"
        val intMethodName = "intShr"
        val longMethodName = "longShr"
        val testClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intShr = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            intShr()
            returnFunc()
        }

        val longShr = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            longShr()
            returnFunc()
        }

        testClass.addMethod(intShr)
        testClass.addMethod(longShr)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(testClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intMethodName }?.invoke(instance, 5, 1)
        assertEquals(2, r1)

        val r2 = clazz.declaredMethods.find { it.name == longMethodName }?.invoke(instance, 10L, 1)
        assertEquals(5L, r2)
    }

    @Test
    fun u_shr() {
        val className = "UShrTest"
        val intMethodName = "intUShr"
        val longMethodName = "longUShr"
        val testClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intUShr = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            unsignedIntShr()
            returnFunc()
        }

        val longUShr = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            unsignedLongShr()
            returnFunc()
        }

        testClass.addMethod(intUShr)
        testClass.addMethod(longUShr)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(testClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intMethodName }?.invoke(instance, 5, 1)
        assertEquals(5 ushr 1, r1)

        val r2 = clazz.declaredMethods.find { it.name == longMethodName }?.invoke(instance, 10L, 1)
        assertEquals(10L ushr 1, r2)
    }

    @Test
    fun and() {
        val className = "AndTest"
        val intMethodName = "intAnd"
        val longMethodName = "longAnd"
        val testClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intAnd = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            intAnd()
            returnFunc()
        }

        val longAnd = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG),
                ParameterDeclaration.fromType("b", TypeDeclaration.LONG)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            longAnd()
            returnFunc()
        }

        testClass.addMethod(intAnd)
        testClass.addMethod(longAnd)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(testClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intMethodName }?.invoke(instance, 5, 1)
        assertEquals(5 and 1, r1)

        val r2 = clazz.declaredMethods.find { it.name == longMethodName }?.invoke(instance, 10L, 2L)
        assertEquals(10L and 2L, r2)
    }

    @Test
    fun or() {
        val className = "OrTest"
        val intMethodName = "intOr"
        val longMethodName = "longOr"
        val testClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intOr = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            intOr()
            returnFunc()
        }

        val longOr = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG),
                ParameterDeclaration.fromType("b", TypeDeclaration.LONG)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            longOr()
            returnFunc()
        }

        testClass.addMethod(intOr)
        testClass.addMethod(longOr)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(testClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intMethodName }?.invoke(instance, 5, 1)
        assertEquals(5 or 1, r1)

        val r2 = clazz.declaredMethods.find { it.name == longMethodName }?.invoke(instance, 10L, 2L)
        assertEquals(10L or 2L, r2)
    }

    @Test
    fun xor() {
        val className = "XorTest"
        val intMethodName = "intXor"
        val longMethodName = "longXor"
        val testClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = className,
            superClass = null,
            interfaces = null
        )

        val intXor = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = intMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.INT),
                ParameterDeclaration.fromType("b", TypeDeclaration.INT)
            ),
            returnType = TypeDeclaration.INT,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            intXor()
            returnFunc()
        }

        val longXor = MethodDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            methodName = longMethodName,
            parameters = arrayOf(
                ParameterDeclaration.fromType("a", TypeDeclaration.LONG),
                ParameterDeclaration.fromType("b", TypeDeclaration.LONG)
            ),
            returnType = TypeDeclaration.LONG,
            throws = null
        ) {
            loadMethodParameter(0)
            loadMethodParameter(1)
            longXor()
            returnFunc()
        }

        testClass.addMethod(intXor)
        testClass.addMethod(longXor)

        val clazz = VertexASM.loadClassFromByteArray(className, VertexASM.writeClassToByteArray(testClass))
        val instance = clazz.constructors.first().newInstance()

        val r1 = clazz.declaredMethods.find { it.name == intMethodName }?.invoke(instance, 5, 1)
        assertEquals(5 xor 1, r1)

        val r2 = clazz.declaredMethods.find { it.name == longMethodName }?.invoke(instance, 10L, 2L)
        assertEquals(10L xor 2L, r2)
    }
}