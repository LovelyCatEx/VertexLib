package com.lovelycatv.vertex.lang.model.element

import com.lovelycatv.vertex.lang.model.KModifierContainer
import com.lovelycatv.vertex.lang.model.KName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationContainer
import com.lovelycatv.vertex.lang.model.type.KTypeMirror

/**
 * Represents a program element such as a class, method, type parameter or variable.
 *
 * @author lovelycat
 * @since 2025-05-29 23:22
 * @version 1.0
 */
interface KElement<T: KTypeMirror> : KAnnotationContainer, KModifierContainer {
    val name: KName

    val simpleName: String get() = this.name.simpleName

    val qualifiedName: String? get() = this.name.qualifiedName

    val typeParameters: List<KTypeParameterElement>

    val packageName: String

    val parentDeclaration: KElement<*>?

    fun asType(): T
}