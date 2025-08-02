package com.lovelycatv.vertex.asm.lang.code.load

import com.lovelycatv.vertex.asm.lang.TypeDeclaration

/**
 * @author lovelycat
 * @since 2025-08-03 01:40
 * @version 1.0
 */
class LoadArray(
    val elementType: TypeDeclaration,
    val dimensions: Int = 1,
    val lengths: Array<Int>
) : ILoadValue {
    init {
        if (this.dimensions <= 0) {
            throw IllegalStateException("Dimensions of array should be great than 0")
        }

        if (this.lengths.size != this.dimensions) {
            throw IllegalStateException("Lengths of every dimension should be specified")
        }
    }
}