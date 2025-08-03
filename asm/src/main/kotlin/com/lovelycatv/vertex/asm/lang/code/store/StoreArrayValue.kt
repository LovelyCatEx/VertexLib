package com.lovelycatv.vertex.asm.lang.code.store

import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.IJavaCode
import com.lovelycatv.vertex.asm.lang.code.load.ILoadValue

/**
 * @author lovelycat
 * @since 2025-08-03 04:13
 * @version 1.0
 */
class StoreArrayValue(
    val elementType: TypeDeclaration,
    val index: Array<out IJavaCode>,
    val newValue: Array<out IJavaCode>
) : IStoreValue