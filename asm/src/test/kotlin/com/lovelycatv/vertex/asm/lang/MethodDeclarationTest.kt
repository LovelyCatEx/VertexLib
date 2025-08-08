package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.JavaModifier
import com.lovelycatv.vertex.asm.assertContainsAll
import com.lovelycatv.vertex.reflect.BaseDataType
import com.lovelycatv.vertex.reflect.TypeUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MethodDeclarationTest {
    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    @Test
    fun fromExpression() {
        val e1 = "public final void simple() throws \$T" to arrayOf<Class<*>>(IllegalStateException::class.java)
        val e2 = "public final \$T simple()" to arrayOf<Class<*>>(Void::class.java)
        val e3 = "public final \$T complex(int a, float b, char c, byte d)" to arrayOf<Class<*>>(String::class.java)
        val e4 = "public final \$T complex(\$T a, float b, char c, byte d)" to arrayOf<Class<*>>(String::class.java, String::class.java)
        val e5 = "private \$T complex(\$T a, \$T... b, \$T[]... c)" to
            arrayOf<Class<*>>(String::class.java, String::class.java, TypeUtils.getArrayClass(String::class.java, 2), String::class.java)

        val m1 = MethodDeclaration.fromExpression(e1.first, *e1.second)
        val m2 = MethodDeclaration.fromExpression(e2.first, *e2.second)
        val m3 = MethodDeclaration.fromExpression(e3.first, *e3.second)
        val m4 = MethodDeclaration.fromExpression(e4.first, *e4.second)
        val m5 = MethodDeclaration.fromExpression(e5.first, *e5.second)

        assertContainsAll(m1.modifiers, JavaModifier.PUBLIC, JavaModifier.FINAL)
        assertContainsAll(m2.modifiers, JavaModifier.PUBLIC, JavaModifier.FINAL)
        assertContainsAll(m3.modifiers, JavaModifier.PUBLIC, JavaModifier.FINAL)
        assertContainsAll(m4.modifiers, JavaModifier.PUBLIC, JavaModifier.FINAL)
        assertContainsAll(m5.modifiers, JavaModifier.PRIVATE)

        assertEquals(Void.TYPE, m1.returnType!!.originalClass)
        assertEquals(Void::class.java, m2.returnType!!.originalClass)
        assertEquals(String::class.java, m3.returnType!!.originalClass)
        assertEquals(String::class.java, m4.returnType!!.originalClass)
        assertEquals(String::class.java, m5.returnType!!.originalClass)

        assertEquals("simple", m1.methodName)
        assertEquals("simple", m2.methodName)
        assertEquals("complex", m3.methodName)
        assertEquals("complex", m4.methodName)
        assertEquals("complex", m5.methodName)

        assertEquals("a", m3.parameters!![0].parameterName)
        assertEquals("b", m3.parameters!![1].parameterName)
        assertEquals("c", m3.parameters!![2].parameterName)
        assertEquals("d", m3.parameters!![3].parameterName)
        assertEquals("a", m4.parameters!![0].parameterName)
        assertEquals("b", m4.parameters!![1].parameterName)
        assertEquals("c", m4.parameters!![2].parameterName)
        assertEquals("d", m4.parameters!![3].parameterName)
        assertEquals("a", m5.parameters!![0].parameterName)
        assertEquals("b", m5.parameters!![1].parameterName)
        assertEquals("c", m5.parameters!![2].parameterName)

        assertEquals(TypeDeclaration.INT.originalClass, m3.parameters!![0].originalClass)
        assertEquals(TypeDeclaration.FLOAT.originalClass, m3.parameters!![1].originalClass)
        assertEquals(TypeDeclaration.CHAR.originalClass, m3.parameters!![2].originalClass)
        assertEquals(TypeDeclaration.BYTE.originalClass, m3.parameters!![3].originalClass)
        assertEquals(TypeDeclaration.STRING.originalClass, m4.parameters!![0].originalClass)
        assertEquals(TypeDeclaration.FLOAT.originalClass, m4.parameters!![1].originalClass)
        assertEquals(TypeDeclaration.CHAR.originalClass, m4.parameters!![2].originalClass)
        assertEquals(TypeDeclaration.BYTE.originalClass, m4.parameters!![3].originalClass)
        assertEquals(TypeDeclaration.STRING.originalClass, m5.parameters!![0].originalClass)
        assertEquals(TypeUtils.getArrayClass(String::class.java, 3), m5.parameters!![1].originalClass)
        assertEquals(TypeUtils.getArrayClass(String::class.java, 2), m5.parameters!![2].originalClass)

        assertContainsAll(m1.throws!!.map { it.originalClass }.toTypedArray(), IllegalStateException::class.java)
    }

    @Test
    fun fromBuilder() {
        val m1 = MethodDeclaration.Builder("testMethod1")
            .addModifier(JavaModifier.PUBLIC)
            .noReturnType()
            .noParameters()
            .noThrows()
            .build()

        assertContainsAll(m1.modifiers, JavaModifier.PUBLIC)
        assertEquals("testMethod1", m1.methodName)
        assertEquals(0, m1.parameters.size)
        assertEquals(TypeDeclaration.VOID, m1.returnType)
        assertEquals(0, m1.throws.size)

        val m2 = MethodDeclaration.Builder("testMethod2")
            .addModifier(JavaModifier.PUBLIC)
            .addModifiers(JavaModifier.FINAL)
            .addParameter(ParameterDeclaration.fromType("a", TypeDeclaration.STRING))
            .addParameters(
                ParameterDeclaration.fromType("b", TypeDeclaration.INT),
                ParameterDeclaration.fromType("c", TypeDeclaration.fromClass(TypeUtils.getArrayClass(BaseDataType.FLOAT_CLASS, 3)))
            )
            .returnType(TypeDeclaration.STRING)
            .addThrow(IllegalStateException::class.java)
            .addThrows(IllegalArgumentException::class.java, NullPointerException::class.java)
            .build()

        assertContainsAll(m2.modifiers, JavaModifier.PUBLIC)
        assertEquals("testMethod2", m2.methodName)
        assertEquals(3, m2.parameters.size)
        assertEquals(TypeDeclaration.STRING, m2.returnType)
        assertEquals(3, m2.throws.size)
        assertContainsAll(m2.throws, *TypeDeclaration.fromClasses(IllegalStateException::class, IllegalArgumentException::class, NullPointerException::class))
    }
}