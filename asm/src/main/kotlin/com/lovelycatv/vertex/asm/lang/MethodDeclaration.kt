package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.JavaModifier
import com.lovelycatv.vertex.asm.lang.code.CodeWriter
import com.lovelycatv.vertex.asm.lang.code.define.FunctionInvocationType
import com.lovelycatv.vertex.asm.toMethodDescriptor
import com.lovelycatv.vertex.reflect.TypeUtils
import com.lovelycatv.vertex.util.StringUtils

/**
 * @author lovelycat
 * @since 2025-08-01 18:38
 * @version 1.0
 */
class MethodDeclaration(
    val modifiers: Array<JavaModifier>,
    val methodName: String,
    parameters: Array<out ParameterDeclaration>? = null,
    returnType: TypeDeclaration? = null,
    throws: Array<TypeDeclaration>? = null,
    fxCodeWriter: (CodeWriter.() -> Unit)? = null
) : CodeContainer() {
    val parameters: Array<out ParameterDeclaration> = parameters ?: emptyArray()
    val returnType: TypeDeclaration = returnType ?: TypeDeclaration.VOID
    val throws: Array<TypeDeclaration> = throws ?: emptyArray()

    init {
        fxCodeWriter?.invoke(super.codeWriter)
    }

    fun isReturnVoid(): Boolean {
        return TypeUtils.isVoid(this.returnType.originalClass)
    }

    fun getDescriptor(): String {
        return this.parameters.toMethodDescriptor(this.returnType)
    }

    companion object {
        /**
         * Create MethodDeclaration by java code expression.
         *
         * @param expression Java class declare expression,
         *                   eg: expression: "public final void testFunc($T msg, int count) throws $T"
         *                       args      : String.class, IllegalStateException.class
         *                   eg: expression: "public void <init>($T a, $T b, float c)"
         *                       args      : String.class, String.class
         * @return ClassDeclaration
         */
        @JvmStatic
        fun fromExpression(expression: String, vararg placeholders: Class<*>, codeWriter: (CodeWriter.() -> Unit)? = null): MethodDeclaration {
            val tExpression = StringUtils.orderReplace(expression, arrayOf("\$T"), *placeholders.map { it.canonicalName }.toTypedArray())

            val indexOfLeftParen = tExpression.indexOf("(")
            val indexOfRightParen = tExpression.indexOf(")")

            // Modifiers, Return Type and Method Name
            val t = tExpression.substring(0..<indexOfLeftParen)

            val arr = t.split(" ").filter { it.isNotBlank() }.map { it.trim() }

            val modifiers = arr.mapNotNull {
                try {
                    JavaModifier.valueOf(it.uppercase())
                } catch (_: Exception) {
                    null
                }
            }.toTypedArray()

            val returnTypeAndMethodName = t.run {
                var t1 = this
                modifiers.forEach {
                    t1 = t1.replace(it.name.lowercase(), "")
                }
                t1
            }

            // ReturnType and MethodName
            val (returnType, methodName) = returnTypeAndMethodName
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.trim() }

            // Parameters
            val parameters = tExpression.substring(indexOfLeftParen + 1..<indexOfRightParen)
                .split(",")
                .filter { it.isNotBlank() }
                .map {
                    val (type, name) = it.split(" ").filter { it.isNotBlank() }.map { it.trim() }
                    val typeDeclaration = if (type.contains("...")) {
                        val elementType = type.replace("...", "")
                        val clazz = if (elementType.contains("[]")) {
                            // Element type is an array too.
                            val realElementType = Class.forName(elementType.replace("[]", ""))
                            val dimensions = elementType.count { it == '[' }
                            TypeUtils.getArrayClass(realElementType, dimensions + 1)
                        } else {
                            TypeUtils.getArrayClass(Class.forName(elementType), 1)
                        }

                        TypeDeclaration.fromClass(clazz)
                    } else {
                        TypeDeclaration.fromName(type)
                    }

                    ParameterDeclaration.fromType(name, typeDeclaration)
                }.toTypedArray()

            // Throws
            val throws = if (tExpression.contains("throws")) {
                tExpression.substring(tExpression.indexOf("throws") + 7..<tExpression.length)
                    .split(",")
                    .filter { it.isNotBlank() }
                    .map { TypeDeclaration.fromName(it.trim()) }
                    .toTypedArray()
            } else emptyArray<TypeDeclaration>()

            val method = MethodDeclaration(
                modifiers = modifiers,
                methodName = methodName,
                parameters = parameters,
                returnType = TypeDeclaration.fromName(returnType),
                throws = throws
            )

            codeWriter?.invoke(method.requireCodeWriter())

            return method
        }

        @JvmStatic
        fun staticInitMethod(
            codeWriter: (CodeWriter.() -> Unit)? = null
        ): MethodDeclaration {
            return MethodDeclaration(
                modifiers = arrayOf(JavaModifier.STATIC),
                methodName = ASMUtils.STATIC_INIT_METHOD_NAME,
                parameters = arrayOf(),
                returnType = TypeDeclaration.VOID,
                throws = arrayOf()
            ).apply {
                writeCode {
                    codeWriter?.invoke(this@apply.codeWriter)

                    returnFunc()
                }
            }
        }

        @JvmStatic
        fun constructor(
            parentClass: ClassDeclaration,
            modifier: JavaModifier = JavaModifier.PUBLIC,
            vararg parameters: ParameterDeclaration,
            superParameters: Array<TypeDeclaration>? = null,
            superArgs: (CodeWriter.() -> Unit)? = null,
            codeWriter: (CodeWriter.() -> Unit)? = null
        ): MethodDeclaration {
            return MethodDeclaration(
                modifiers = arrayOf(modifier),
                methodName = ASMUtils.CONSTRUCTOR_NAME,
                parameters = parameters,
                returnType = TypeDeclaration.VOID,
                throws = arrayOf()
            ).apply {
                writeCode {
                    if (parentClass.isNoSuperClass()) {
                        invokeMethod(
                            type = FunctionInvocationType.SUPER,
                            owner = ASMUtils.OBJECT_CLASS,
                            methodName = ASMUtils.CONSTRUCTOR_NAME,
                            returnType = TypeDeclaration.VOID
                        )
                    } else {
                        invokeMethod(
                            type = FunctionInvocationType.SUPER,
                            owner = parentClass.superClass.originalClass,
                            methodName = ASMUtils.CONSTRUCTOR_NAME,
                            parameters = superParameters ?: emptyArray(),
                            returnType = TypeDeclaration.VOID
                        ) {
                            superArgs?.invoke(this)
                        }
                    }

                    codeWriter?.invoke(this@apply.codeWriter)

                    returnFunc()
                }
            }
        }

        @JvmStatic
        fun noArgsConstructor(
            parentClass: ClassDeclaration,
            modifier: JavaModifier = JavaModifier.PUBLIC,
            codeWriter: (CodeWriter.() -> Unit)? = null
        ): MethodDeclaration {
            return MethodDeclaration(
                modifiers = arrayOf(modifier),
                methodName = ASMUtils.CONSTRUCTOR_NAME,
                parameters = arrayOf(),
                returnType = TypeDeclaration.VOID,
                throws = arrayOf()
            ).apply {
                writeCode {
                    if (parentClass.isNoSuperClass()) {
                        invokeMethod(
                            type = FunctionInvocationType.SUPER,
                            owner = ASMUtils.OBJECT_CLASS,
                            methodName = ASMUtils.CONSTRUCTOR_NAME,
                            returnType = TypeDeclaration.VOID
                        )
                    } else {
                        invokeMethod(
                            type = FunctionInvocationType.SUPER,
                            owner = parentClass.superClass.originalClass,
                            methodName = ASMUtils.CONSTRUCTOR_NAME,
                            returnType = TypeDeclaration.VOID
                        )
                    }

                    codeWriter?.invoke(this@apply.codeWriter)

                    returnFunc()
                }
            }
        }
    }

    class Builder(private var methodName: String) {
        private val modifiers: MutableList<JavaModifier> = mutableListOf()
        private val parameters: MutableList<ParameterDeclaration> = mutableListOf()
        private var returnType: TypeDeclaration? = null
        private val throws: MutableList<TypeDeclaration> = mutableListOf()
        private var fxCodeWriter: (CodeWriter.() -> Unit)? = null

        fun addModifier(modifier: JavaModifier): Builder {
            this.modifiers.add(modifier)
            return this
        }

        fun addModifiers(vararg modifiers: JavaModifier): Builder {
            this.modifiers.addAll(modifiers)
            return this
        }

        fun methodName(name: String): Builder {
            this.methodName = name
            return this
        }

        fun noParameters(): Builder {
            this.parameters.clear()
            return this
        }

        fun addParameter(parameter: ParameterDeclaration): Builder {
            this.parameters.add(parameter)
            return this
        }

        fun addParameters(vararg parameters: ParameterDeclaration): Builder {
            this.parameters.addAll(parameters)
            return this
        }

        fun noReturnType(): Builder {
            this.returnType = null
            return this
        }

        fun returnType(returnType: TypeDeclaration): Builder {
            this.returnType = returnType
            return this
        }

        fun returnType(clazz: Class<*>): Builder {
            this.returnType = TypeDeclaration.fromClass(clazz)
            return this
        }

        fun addThrow(exceptionType: TypeDeclaration): Builder {
            this.throws.add(exceptionType)
            return this
        }

        fun noThrows(): Builder {
            this.throws.clear()
            return this
        }

        fun addThrow(exceptionType: Class<*>): Builder {
            this.throws.add(TypeDeclaration.fromClass(exceptionType))
            return this
        }

        fun addThrows(vararg exceptionTypes: TypeDeclaration): Builder {
            this.throws.addAll(exceptionTypes)
            return this
        }

        fun addThrows(vararg exceptionTypes: Class<*>): Builder {
            this.throws.addAll(exceptionTypes.map { TypeDeclaration.fromClass(it) })
            return this
        }

        fun writeCode(block: CodeWriter.() -> Unit): Builder {
            this.fxCodeWriter = block
            return this
        }

        fun build() = MethodDeclaration(
            modifiers = modifiers.toTypedArray(),
            methodName = methodName,
            parameters = parameters.toTypedArray(),
            returnType = returnType,
            throws = throws.toTypedArray(),
            fxCodeWriter = fxCodeWriter
        )
    }
}