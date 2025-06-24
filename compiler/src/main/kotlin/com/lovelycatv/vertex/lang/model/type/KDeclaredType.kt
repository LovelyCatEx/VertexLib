package com.lovelycatv.vertex.lang.model.type

import com.lovelycatv.vertex.lang.model.element.KDeclaredTypeElement

/**
 * Represents a declared type, either a class type or an interface type. This includes parameterized types such as [java.util.Set]<[String]> as well as raw types.
 *
 * @author lovelycat
 * @since 2025-05-29 23:36
 * @version 1.0
 */
interface KDeclaredType : KReferenceType {
    val parentDeclaredType: KDeclaredType?

    /**
     * If the declared type refers to a exactly class instance (such as [List] ([String])),
     * then the typeArguments will be a [KDeclaredType]: [String].
     *
     * Otherwise typeArguments of List<T> will be a [KTypeVariable].
     */
    val typeArguments: List<KTypeMirror>

    /**
     * Get the element corresponding to this type.
     *
     * @return The element corresponding to this type.
     */
    fun asElement(): KDeclaredTypeElement

    override fun inspect(): List<String> {
        return super.inspect() + listOf(
            this.toString(),
            "  Type Arguments: " + this.typeArguments.joinToString(separator = ", ", prefix = "<", postfix = ">") {
                it.inspect().joinToString(separator = " ", prefix = "[", postfix = "]")
            }
        )
    }
}