package com.lovelycatv.vertex.asm

import com.lovelycatv.vertex.asm.lang.TypeDeclaration

/**
 * @author lovelycat
 * @since 2025-08-07 12:35
 * @version 1.0
 */
object Intrinsics {
    fun require(condition: Boolean, message: String = "") {
        if (!condition) {
            throw IllegalArgumentException(message)
        }
    }

    fun requirePrimitiveType(clazz: Class<*>) {
        require(ASMUtils.isPrimitiveType(clazz), "${clazz.canonicalName} should be a primitive type.")
    }

    fun requirePrimitiveType(type: TypeDeclaration) {
        requirePrimitiveType(type.originalClass)
    }

    fun throwImpossibleStateException(): Nothing {
        throw IllegalStateException("Impossible :(")
    }
}