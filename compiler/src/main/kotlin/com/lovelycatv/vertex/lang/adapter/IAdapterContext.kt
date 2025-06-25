package com.lovelycatv.vertex.lang.adapter

import com.lovelycatv.vertex.lang.model.annotation.KAnnotated
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.*
import com.lovelycatv.vertex.lang.model.type.*

/**
 * @author lovelycat
 * @since 2025-06-01 18:39
 * @version 1.0
 */
interface IAdapterContext<A: Any, E: Any, T: Any> {
    fun translateAnnotation(annotation: A): KAnnotationMirror

    fun translateAnnotated(annotated: Any): KAnnotated

    fun translateElement(element: E): KElement<*>

    fun translateTypeElement(element: E): KDeclaredTypeElement

    fun translateTypeParameterElement(element: E): KTypeParameterElement

    fun translateExecutableElement(element: E): KExecutableElement

    fun translateVariableElement(element: E): KVariableElement<KTypeMirror>

    fun translateType(type: T): KTypeMirror

    fun translateDeclaredType(type: T): KDeclaredType

    fun translateExecutableType(type: T): KExecutableType

    fun translatePrimitiveType(type: T): KPrimitiveType

    fun translateTypeVariable(type: T): KTypeVariable

    fun translateWildcardType(type: T): KWildcardType

    fun translateArrayType(type: T): KArrayType
}