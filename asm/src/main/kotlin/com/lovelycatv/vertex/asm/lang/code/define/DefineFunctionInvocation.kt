package com.lovelycatv.vertex.asm.lang.code.define

import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.FunctionInvocationType
import com.lovelycatv.vertex.asm.lang.code.load.ILoadValue
import com.lovelycatv.vertex.asm.toMethodDescriptor
import com.lovelycatv.vertex.reflect.ReflectUtils

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

    fun hasReturnType(): Boolean = ReflectUtils.isVoid(this.returnType.originalClass)

    fun getDescriptor(): String {
        return this.parameters.toMethodDescriptor(this.returnType)
    }
}