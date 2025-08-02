package com.lovelycatv.vertex.asm.lang.code.store

import com.lovelycatv.vertex.asm.lang.TypeDeclaration

/**
 * @author lovelycat
 * @since 2025-08-02 17:04
 * @version 1.0
 */
class StoreFieldVariable(
    val targetClass: Class<*>?,
    val fieldName: String,
    val fieldType: TypeDeclaration,
    val isStatic: Boolean
) : IStoreValue