package com.lovelycatv.vertex.lang.model.type

/**
 * Represents a wildcard type argument.
 * Examples include:
 *
 *   ?
 *
 *   ? extends Number
 *
 *   ? super T
 *
 * @author lovelycat
 * @since 2025-05-30 14:29
 * @version 1.0
 */
interface KWildcardType : KTypeMirror {
    val extendsBound: KTypeMirror?
    val superBound: KTypeMirror?

    override fun inspect() = listOf(
        ""
    )
}