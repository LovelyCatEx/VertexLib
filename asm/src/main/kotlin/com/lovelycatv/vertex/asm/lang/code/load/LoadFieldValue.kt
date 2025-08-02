package com.lovelycatv.vertex.asm.lang.code.load

/**
 * @author lovelycat
 * @since 2025-08-02 18:20
 * @version 1.0
 */
class LoadFieldValue(
    val targetClass: Class<*>?,
    val fieldName: String,
    val fieldType: Class<*>,
    val isStatic: Boolean
) : ILoadValue