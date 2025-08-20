package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.VertexASM
import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.reflect.JavaModifier
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * @author lovelycat
 * @since 2025-08-07 15:19
 * @version 1.0
 */
class StaticInitMethodTest {
    @BeforeEach
    fun beforeEach() {
        VertexASMLog.setEnableDebugging(true)
    }

    @Test
    fun test() {
        val testClassName = "TestClass"
        val testClass = ClassDeclaration.fromExpression("public static class $testClassName")
        testClass.addField(FieldDeclaration(arrayOf(JavaModifier.PUBLIC), "a", TypeDeclaration.STRING))
        testClass.addField(FieldDeclaration(arrayOf(JavaModifier.PUBLIC, JavaModifier.STATIC), "b", TypeDeclaration.DOUBLE))
        testClass.setStaticInitMethod(MethodDeclaration.staticInitMethod {
            storeField(null, "b", TypeDeclaration.DOUBLE, true) {
                loadConstant(1.0)
                pop(2)
                loadConstant(3.0)
            }
        })

        testClass.addMethod(MethodDeclaration.noArgsConstructor(parentClass = testClass, JavaModifier.PUBLIC) {
            storeField(null, "a", TypeDeclaration.STRING, false) {
                loadConstant("Hello, World! 233")
                pop(1)
                loadConstant("Hello, World!")
            }
        })

        val loadedClass = VertexASM.loadClassFromByteArray(testClassName, VertexASM.writeClassToByteArray(testClass))
        val instance = loadedClass.constructors.first().newInstance()

        assertEquals("Hello, World!", loadedClass.declaredFields.find { it.name == "a" }?.get(instance))
        assertEquals(3.0, loadedClass.declaredFields.find { it.name == "b" }?.get(null))
    }
}