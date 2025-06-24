package com.lovelycatv.vertex.lang.model.type

/**
 * Represents an array type. A multidimensional array type is represented as an array type whose component type is also an array type.
 *
 * @author lovelycat
 * @since 2025-05-29 23:39
 * @version 1.0
 */
interface KArrayType : KReferenceType {
    /**
     * As the type of element could be primitive or reference type,
     * the actual type of this [KTypeMirror] may be [KPrimitiveType] or [KReferenceType]
     */
    val elementType: KTypeMirror

    override fun inspect(): List<String> {
        return super.inspect() + listOf()
    }
}