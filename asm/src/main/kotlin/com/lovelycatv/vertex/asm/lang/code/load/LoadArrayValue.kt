package com.lovelycatv.vertex.asm.lang.code.load

import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.IJavaCode

/**
 * @author lovelycat
 * @since 2025-08-03 04:13
 * @version 1.0
 */
class LoadArrayValue(
    val elementType: TypeDeclaration,
    val index: Array<out IJavaCode>
) : ILoadValue