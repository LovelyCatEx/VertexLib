package com.lovelycatv.vertex.asm.lang.code

import com.lovelycatv.vertex.asm.lang.TypeDeclaration

/**
 * @author lovelycat
 * @since 2025-08-20 17:08
 * @version 1.0
 */
class NumberCompare(val numberType: TypeDeclaration, val infiniteMaximum: Boolean = true) : IJavaCode {
    constructor(numberType: TypeDeclaration) : this(numberType, true)

    init {
        if (this.numberType.originalClass !in SUPPORTED_TYPES) {
            throw IllegalArgumentException("NumberCompare supports Long, Float and Double only.")
        }
    }

    companion object {
        private val SUPPORTED_TYPES = arrayOf(Long::class.java, Float::class.java, Double::class.java)
    }
}