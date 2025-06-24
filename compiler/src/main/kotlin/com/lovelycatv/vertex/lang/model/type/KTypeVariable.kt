package com.lovelycatv.vertex.lang.model.type

import com.lovelycatv.vertex.lang.model.element.KTypeParameterElement

/**
 * Represents a type variable. A type variable may be explicitly declared by a [com.lovelycatv.vertex.lang.model.element.KTypeParameterElement] of a type, method, or constructor.
 *
 * @author lovelycat
 * @since 2025-05-29 23:39
 * @version 1.0
 */
interface KTypeVariable : KReferenceType {
    val upperBounds: Sequence<KReferenceType>

    /**
     * True when a type variable is the condition: [List] (T) or [List] ([Object])
     */
    val isExactlyOne get() = this.upperBounds.toList().size == 1

    /**
     * True when a type variable is the condition: [List] (T extends [Object])
     */
    val isExactlyDeclaredType get() = this.isExactlyOne && this.upperBounds.iterator().next() is KDeclaredType

    /**
     * Get the element corresponding to this type.
     *
     * @return The element corresponding to this type.
     */
    fun asElement(): KTypeParameterElement

    override fun inspect(): List<String> {
        return super.inspect() + listOf(
            this.upperBounds.joinToString(separator = " & ", prefix = "", postfix = "") { it.toString() }
        )
    }
}