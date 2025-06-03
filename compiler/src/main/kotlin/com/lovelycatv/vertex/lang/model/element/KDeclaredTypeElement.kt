package com.lovelycatv.vertex.lang.model.element

import com.lovelycatv.vertex.lang.model.type.KDeclaredType

/**
 * Represents a class or interface program element.
 * Provides access to information about the class or interface and its members.
 * Note that an enum class and a record class are specialized kinds of classes and an annotation interface is a specialized kind of interface.
 *
 * @author lovelycat
 * @since 2025-05-29 23:10
 * @version 1.0
 */
interface KDeclaredTypeElement : KElement<KDeclaredType>, KElementContainer {
    val superClass: KDeclaredType

    val interfaces: Sequence<KDeclaredType>
}