package com.lovelycatv.vertex.asm.lang.code.define

import com.lovelycatv.vertex.asm.lang.TypeDeclaration

/**
 * @author lovelycat
 * @since 2025-08-02 03:19
 * @version 1.0
 */
class DefineLocalVariable(
    val type: TypeDeclaration,
    val name: String,
    val isFinal: Boolean
) : IDefinition