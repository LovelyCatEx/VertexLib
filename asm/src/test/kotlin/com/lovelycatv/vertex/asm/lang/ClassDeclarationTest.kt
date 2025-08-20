package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.assertContainsAll
import com.lovelycatv.vertex.reflect.JavaModifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.Serializable

class ClassDeclarationTest {
    @Test
    fun fromExpression() {
        val e1 = "public class TestClass1" to arrayOf<Class<*>>()
        val e2 = "public static class TestClass2" to arrayOf<Class<*>>()
        val e3 = "public abstract class TestClass3" to arrayOf<Class<*>>()
        val e4 = "public static class TestClass4 extends \$T" to arrayOf<Class<*>>(ASMUtils.OBJECT_CLASS)
        val e5 = "public static class TestClass5 extends     \$T" to arrayOf<Class<*>>(ASMUtils.OBJECT_CLASS)
        val e6 = "public static class TestClass6 implements \$T" to arrayOf<Class<*>>(Serializable::class.java)
        val e7 = "public static class TestClass7 implements   \$T" to arrayOf<Class<*>>(Serializable::class.java)
        val e8 = "public final class TestClass8 implements \$T, \$T" to arrayOf<Class<*>>(Serializable::class.java, Comparable::class.java)
        val e9 = "public final class TestClass9 implements \$T,     \$T" to arrayOf<Class<*>>(Serializable::class.java, Comparable::class.java)
        val e10 = "public static class TestClass10 extends \$T implements \$T,     \$T" to arrayOf<Class<*>>(ASMUtils.OBJECT_CLASS, Serializable::class.java, Comparable::class.java)

        val c1 = ClassDeclaration.fromExpression(e1.first, *e1.second)
        val c2 = ClassDeclaration.fromExpression(e2.first, *e2.second)
        val c3 = ClassDeclaration.fromExpression(e3.first, *e3.second)
        val c4 = ClassDeclaration.fromExpression(e4.first, *e4.second)
        val c5 = ClassDeclaration.fromExpression(e5.first, *e5.second)
        val c6 = ClassDeclaration.fromExpression(e6.first, *e6.second)
        val c7 = ClassDeclaration.fromExpression(e7.first, *e7.second)
        val c8 = ClassDeclaration.fromExpression(e8.first, *e8.second)
        val c9 = ClassDeclaration.fromExpression(e9.first, *e9.second)
        val c10 = ClassDeclaration.fromExpression(e10.first, *e10.second)

        assertContainsAll(c1.modifiers, JavaModifier.PUBLIC)
        assertContainsAll(c2.modifiers, JavaModifier.PUBLIC, JavaModifier.STATIC)
        assertContainsAll(c3.modifiers, JavaModifier.PUBLIC, JavaModifier.ABSTRACT)
        assertContainsAll(c4.modifiers, JavaModifier.PUBLIC, JavaModifier.STATIC)
        assertContainsAll(c5.modifiers, JavaModifier.PUBLIC, JavaModifier.STATIC)
        assertContainsAll(c6.modifiers, JavaModifier.PUBLIC, JavaModifier.STATIC)
        assertContainsAll(c7.modifiers, JavaModifier.PUBLIC, JavaModifier.STATIC)
        assertContainsAll(c8.modifiers, JavaModifier.PUBLIC, JavaModifier.FINAL)
        assertContainsAll(c9.modifiers, JavaModifier.PUBLIC, JavaModifier.FINAL)
        assertContainsAll(c10.modifiers, JavaModifier.PUBLIC, JavaModifier.STATIC)

        assertEquals("TestClass1", c1.className)
        assertEquals("TestClass2", c2.className)
        assertEquals("TestClass3", c3.className)
        assertEquals("TestClass4", c4.className)
        assertEquals("TestClass5", c5.className)
        assertEquals("TestClass6", c6.className)
        assertEquals("TestClass7", c7.className)
        assertEquals("TestClass8", c8.className)
        assertEquals("TestClass9", c9.className)
        assertEquals("TestClass10", c10.className)

        assertEquals(TypeDeclaration.OBJECT, c1.superClass)
        assertEquals(TypeDeclaration.OBJECT, c2.superClass)
        assertEquals(TypeDeclaration.OBJECT, c3.superClass)
        assertEquals(TypeDeclaration.OBJECT, c4.superClass)
        assertEquals(TypeDeclaration.OBJECT, c5.superClass)
        assertEquals(TypeDeclaration.OBJECT, c6.superClass)
        assertEquals(TypeDeclaration.OBJECT, c7.superClass)
        assertEquals(TypeDeclaration.OBJECT, c8.superClass)
        assertEquals(TypeDeclaration.OBJECT, c9.superClass)
        assertEquals(TypeDeclaration.OBJECT, c10.superClass)

        assertEquals(0, c1.interfaces.size)
        assertEquals(0, c2.interfaces.size)
        assertEquals(0, c3.interfaces.size)
        assertEquals(0, c4.interfaces.size)
        assertEquals(0, c5.interfaces.size)
        assertContainsAll(c6.interfaces!!, TypeDeclaration.fromClass(Serializable::class.java))
        assertContainsAll(c7.interfaces!!, TypeDeclaration.fromClass(Serializable::class.java))
        assertContainsAll(c8.interfaces!!, TypeDeclaration.fromClass(Serializable::class.java), TypeDeclaration.fromClass(Comparable::class.java))
        assertContainsAll(c9.interfaces!!, TypeDeclaration.fromClass(Serializable::class.java), TypeDeclaration.fromClass(Comparable::class.java))
        assertContainsAll(c10.interfaces!!, TypeDeclaration.fromClass(Serializable::class.java), TypeDeclaration.fromClass(Comparable::class.java))
    }

    @Test
    fun fromBuilder() {
        val testClass1 = ClassDeclaration.Builder("TestClass1")
            .addModifier(JavaModifier.PUBLIC)
            .noSuperClass()
            .noInterfaces()
            .build()

        assertContainsAll(testClass1.modifiers, JavaModifier.PUBLIC)
        assertEquals("TestClass1", testClass1.className)
        assertEquals(TypeDeclaration.OBJECT, testClass1.superClass)
        assertEquals(0, testClass1.interfaces.size)

        val testClass2 = ClassDeclaration.Builder("TestClass2")
            .addModifiers(JavaModifier.PUBLIC, JavaModifier.FINAL)
            .superClass(CharSequence::class.java)
            .addInterface(Comparable::class.java)
            .addInterfaces(Serializable::class.java, Cloneable::class.java)
            .build()

        assertContainsAll(testClass2.modifiers, JavaModifier.PUBLIC)
        assertEquals("TestClass2", testClass2.className)
        assertEquals(TypeDeclaration.fromClass(CharSequence::class.java), testClass2.superClass)
        assertContainsAll(testClass2.interfaces, *TypeDeclaration.fromClasses(Comparable::class, Serializable::class, Cloneable::class))
    }
}