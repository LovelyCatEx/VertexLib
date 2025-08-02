package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.JavaModifier
import com.lovelycatv.vertex.asm.lang.code.CodeWriter
import com.lovelycatv.vertex.asm.lang.code.FunctionInvocationType

/**
 * @author lovelycat
 * @since 2025-08-01 18:38
 * @version 1.0
 */
class MethodDeclaration(
    val modifiers: Array<JavaModifier>,
    val methodName: String,
    val parameters: Array<out ParameterDeclaration>?,
    val returnType: TypeDeclaration?,
    val throws: Array<TypeDeclaration>?
) : CodeContainer() {
    companion object {
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
                        superArgs?.invoke(this)
                        invokeMethod(
                            type = FunctionInvocationType.SUPER,
                            owner = parentClass.superClass!!.type,
                            methodName = ASMUtils.CONSTRUCTOR_NAME,
                            parameters = superParameters ?: emptyArray(),
                            returnType = TypeDeclaration.VOID
                        )
                    }

                    codeWriter?.invoke(this@apply.codeWriter)

                    returnFunc()
                }
            }
        }

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
                            owner = parentClass.superClass!!.type,
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

    val actualParameters: Array<out ParameterDeclaration> get() = this.parameters ?: emptyArray()
    val actualReturnType: TypeDeclaration get() = this.returnType ?: TypeDeclaration.VOID
    val actualThrows: Array<TypeDeclaration> get() = this.throws ?: emptyArray()

    fun getDescriptor(): String {
        return "${actualParameters.joinToString(separator = "", prefix = "(", postfix = ")") { it.getDescriptor() }}${actualReturnType.getDescriptor()}"
    }
}