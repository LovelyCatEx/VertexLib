package com.lovelycatv.vertex.lang.model

import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.type.DeclaredType

/**
 * @author lovelycat
 * @since 2025-05-27 18:50
 * @version 1.0
 */
class AnnotationUtils private constructor()

fun AnnotationMirror.compareType(annotationClazz: Class<out Annotation>): Boolean {
    return this.annotationType.toString() == annotationClazz.name
}

@Suppress("UNCHECKED_CAST")
fun AnnotationMirror.getClassFieldValue(fieldName: String, isArray: Boolean): List<DeclaredType> {
    val result = mutableListOf<DeclaredType>()

    val value = this.elementValues.toList().find { (field, _) -> field.simpleName.toString() == fieldName }?.second
        ?: return emptyList()

    if (isArray) {
        result.addAll((value.value as List<AnnotationValue>).map { it.value as DeclaredType })
    } else {
        result.add(value.value as DeclaredType)
    }

    return result
}

@Suppress("UNCHECKED_CAST")
fun <R> AnnotationMirror.getFieldValue(fieldName: String): R? {
    val value = this.elementValues.toList().find { (field, _) -> field.simpleName.toString() == fieldName }?.second
        ?: return null

    return value.value as R
}