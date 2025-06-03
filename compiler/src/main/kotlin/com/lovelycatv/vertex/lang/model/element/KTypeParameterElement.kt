package com.lovelycatv.vertex.lang.model.element

import com.lovelycatv.vertex.lang.model.type.KReferenceType
import com.lovelycatv.vertex.lang.model.type.KTypeVariable
import com.lovelycatv.vertex.lang.modifier.IModifier

/**
 * Represents a formal type parameter of a generic class, interface, method, or constructor element. A type parameter declares a [KTypeVariable].
 * @see KTypeVariable
 *
 * @author lovelycat
 * @since 2025-05-29 23:31
 * @version 1.0
 */
interface KTypeParameterElement : KElement<KTypeVariable> {
    /**
     * Returns the bounds of this type parameter.
     */
    val upperBounds: Sequence<KReferenceType> get() = this.asType().upperBound

    /**
     * The generic class, interface, method, or constructor that is parameterized by this type parameter.
     */
    val genericElement: KElement<*>

    /**
     * If this represents a kotlin type parameter, then this type parameter may has reified modifier.
     * Otherwise the modifiers will always be empty.
     */
    override val modifiers: Sequence<IModifier>
}