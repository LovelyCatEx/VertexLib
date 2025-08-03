package com.lovelycatv.vertex.asm.lang

/**
 * @author lovelycat
 * @since 2025-08-01 18:41
 * @version 1.0
 */
class ParameterDeclaration(
    val parameterName: String,
    type: Class<*>,
    isArray: Boolean,
    arrayDimensions: Int,
    originalClass: Class<*>
) : TypeDeclaration(type, isArray, arrayDimensions, originalClass) {
    companion object {
        fun fromType(parameterName: String, type: TypeDeclaration): ParameterDeclaration {
            return ParameterDeclaration(parameterName, type.type, type.isArray, type.arrayDimensions, type.originalClass)
        }
    }
}