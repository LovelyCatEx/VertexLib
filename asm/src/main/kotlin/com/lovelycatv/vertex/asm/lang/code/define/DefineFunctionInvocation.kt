package com.lovelycatv.vertex.asm.lang.code.define

import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.FunctionInvocationType
import com.lovelycatv.vertex.asm.lang.code.load.ILoadValue

/**
 * @author lovelycat
 * @since 2025-08-02 03:32
 * @version 1.0
 */
class DefineFunctionInvocation(
    val type: FunctionInvocationType,
    val owner: Class<*>,
    val methodName: String,
    val parameters: Array<TypeDeclaration> = arrayOf(),
    val returnType: TypeDeclaration = TypeDeclaration.VOID,
    val args: Array<out ILoadValue> = arrayOf()
) : IDefinition {
    constructor(type: FunctionInvocationType, owner: Class<*>, methodName: String, parameters: Array<TypeDeclaration>, returnType: Class<*>, args: Array<out ILoadValue>)
        : this(type, owner, methodName, parameters, TypeDeclaration.fromClass(returnType), args)

    fun hasReturnType(): Boolean = this.returnType.type == Void::class.java

    fun getDescriptor(): String {
        return "${parameters.joinToString(separator = "", prefix = "(", postfix = ")") { it.getDescriptor() }}${returnType.getDescriptor()}"
    }
}