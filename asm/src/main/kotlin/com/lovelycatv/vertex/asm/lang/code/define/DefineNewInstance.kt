package com.lovelycatv.vertex.asm.lang.code.define

import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.load.ILoadValue

/**
 * @author lovelycat
 * @since 2025-08-02 03:34
 * @version 1.0
 */
class DefineNewInstance(
    val clazz: Class<*>,
    val constructorParameters: Array<TypeDeclaration> = arrayOf(),
    val args: Array<out ILoadValue> = arrayOf()
) : IDefinition {
    fun getConstructorDescriptor(): String {
        return "${constructorParameters.joinToString(separator = "", prefix = "(", postfix = ")") { it.getDescriptor() }}V"
    }
}