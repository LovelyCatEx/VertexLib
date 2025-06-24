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

    override fun inspect(): List<String> {
        return super.inspect() + listOf(
            if (extendsBound != null) {
                "out ${extendsBound!!}"
            } else if (superBound != null) {
                "in ${superBound!!}"
            } else {
                "UNKNOWN_BOUND"
            }
        )
    }
}