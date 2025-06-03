package com.lovelycatv.vertex.lang.model.element

import com.lovelycatv.vertex.lang.model.type.KTypeMirror

/**
 * Represents a field, enum constant, method or constructor parameter or local variable.
 *
 * @author lovelycat
 * @since 2025-05-29 23:53
 * @version 1.0
 */
interface KVariableElement<T: KTypeMirror> : KElement<T> {
    /**
     * Returns the type of this variable.
     *
     * @return The type of this variable.
     */
    override fun asType(): T

    /**
     * Returns the value of this variable if this is a final field initialized to a compile-time constant. Returns null otherwise.
     * The value will be of a primitive type or a [String].
     * If the value is of a primitive type, it is wrapped in the appropriate wrapper class (such as [Integer]).
     *
     * Note that not all final fields will have constant values.
     * In particular, enum constants are not considered to be compile-time constants.
     * To have a constant value, a field's type must be either a primitive type or [String].
     */
    val constantValue: Any?
}