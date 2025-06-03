package com.lovelycatv.vertex.lang.adapter

import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.*
import com.lovelycatv.vertex.lang.model.type.*

/**
 * @author lovelycat
 * @since 2025-06-01 18:39
 * @version 1.0
 */
abstract class AbstractAdapterContext<A: Any, E: Any, T: Any> {
    abstract fun translateAnnotation(annotation: A): KAnnotationMirror

    abstract fun translateElement(element: E): KElement<*>

    abstract fun translateTypeElement(element: E): KDeclaredTypeElement

    abstract fun translateTypeParameterElement(element: E): KTypeParameterElement

    abstract fun translateExecutableElement(element: E): KExecutableElement

    abstract fun translateVariableElement(element: E): KVariableElement<KTypeMirror>

    abstract fun translateType(type: T): KTypeMirror

    abstract fun translateDeclaredType(type: T): KDeclaredType

    abstract fun translateExecutableType(type: T): KExecutableType

    abstract fun translatePrimitiveType(type: T): KPrimitiveType

    abstract fun translateTypeVariable(type: T): KTypeVariable

    abstract fun translateWildcardType(type: T): KWildcardType

    abstract fun translateArrayType(type: T): KArrayType
}