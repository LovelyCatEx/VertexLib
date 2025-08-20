package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.reflect.JavaModifier

/**
 * @author lovelycat
 * @since 2025-08-01 18:44
 * @version 1.0
 */
class FieldDeclaration(
    val modifiers: Array<JavaModifier>,
    val name: String,
    val type: TypeDeclaration,
    val defaultValue: Any? = null
)