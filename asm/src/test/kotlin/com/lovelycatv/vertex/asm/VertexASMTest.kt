package com.lovelycatv.vertex.asm

import com.lovelycatv.vertex.asm.lang.*
import com.lovelycatv.vertex.asm.lang.code.define.FunctionInvocationType
import com.lovelycatv.vertex.asm.loader.ByteClassLoader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.PrintStream

class VertexASMTest {
    private val testClass: ClassDeclaration
    private var testClassByteCode: ByteArray? = null

    init {
        testClass = ClassDeclaration(
            modifiers = arrayOf(JavaModifier.PUBLIC),
            className = "TestClass",
            superClass = null,
            interfaces = null
        )

        testClass.addField(FieldDeclaration(arrayOf(JavaModifier.PRIVATE), "userName", TypeDeclaration.STRING, null))
        testClass.addField(FieldDeclaration(arrayOf(JavaModifier.PUBLIC, JavaModifier.STATIC), "userCount", TypeDeclaration.INT, 0))

        testClass.addMethod(
            MethodDeclaration.constructor(
                parentClass = testClass,
                modifier = JavaModifier.PUBLIC,
                parameters = arrayOf(ParameterDeclaration.fromType("userName", TypeDeclaration.STRING))
            ) {
                storeField(null, "userName", TypeDeclaration.STRING, false) {
                    loadMethodParameter(0)
                }

                loadStaticField(null, "userCount", Int::class.java)

            }
        )

        testClass.addMethod(
            MethodDeclaration(
                modifiers = arrayOf(JavaModifier.PUBLIC),
                parameters = arrayOf(),
                methodName = "sayHello",
                returnType = TypeDeclaration.STRING,
                throws = null
            ).apply {
                writeCode {
                    // final StringBuilder sb = new StringBuilder();
                    defineFinalVariable("sb", StringBuilder::class.java)
                    newInstance(StringBuilder::class.java)
                    storeVariable("sb")

                    // sb.append("Hello, ");
                    loadVariable("sb")
                    invokeMethod(
                        owner = StringBuilder::class.java,
                        methodName= "append",
                        parameters = arrayOf(TypeDeclaration.STRING),
                        returnType = StringBuilder::class.java
                    ) { loadConstant("Hello, ") }

                    // sb.append(this.userName);
                    invokeMethod(
                        owner = StringBuilder::class.java,
                        methodName= "append",
                        parameters = arrayOf(TypeDeclaration.STRING),
                        returnType = StringBuilder::class.java
                    ) { loadField(null, "userName", String::class.java) }

                    // sb.append("!");
                    invokeMethod(
                        owner = StringBuilder::class.java,
                        methodName= "append",
                        parameters = arrayOf(TypeDeclaration.STRING),
                        returnType = StringBuilder::class.java
                    ) { loadConstant("!") }

                    pop(1)

                    // final result = sb.toString()
                    defineFinalVariable("result", String::class.java)
                    loadVariable("sb")
                    invokeMethod(FunctionInvocationType.NORMAL, StringBuilder::toString)
                    storeVariable("result")

                    loadStaticField(System::out)
                    invokeMethod(
                        owner = PrintStream::class.java,
                        methodName= "println",
                        parameters = arrayOf(TypeDeclaration.STRING),
                        returnType = TypeDeclaration.VOID
                    ) { loadVariable("result") }

                    loadVariable("result")
                    returnFunc()
                }
            }
        )
    }

    @Test
    fun writeClassToByteArray() {
        testClassByteCode = VertexASM.writeClassToByteArray(testClass)
    }

    @Test
    fun loadClassFromByteArray() {
        if (testClassByteCode == null) {
            writeClassToByteArray()
        }

        assertTrue(testClassByteCode != null)

        val loader = ByteClassLoader(testClass.className, testClassByteCode!!)
        val clazz = loader.loadClass(testClass.className)

        assertTrue(loader.classLoaded())

        testClass.methods.forEach { method ->
            assertTrue(method.methodName == ASMUtils.CONSTRUCTOR_NAME || clazz.declaredMethods.any { it.name == method.methodName })
        }

        testClass.fields.forEach { field ->
            assertTrue(clazz.declaredFields.any { it.name == field.name })
        }

        val instance = clazz.constructors[0].newInstance("Vertex ASM")
        val sayHelloMethod = clazz.getMethod("sayHello")

        assertEquals("Hello, Vertex ASM!", sayHelloMethod.invoke(instance))
    }
}