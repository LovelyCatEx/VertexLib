package com.lovelycatv.vertex.lang.model.annotation

import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-05-29 23:20
 * @version 1.0
 */
interface KAnnotated {
    val annotations: Sequence<KAnnotationMirror>

    fun <A: Annotation> getAnnotationByType(clazz: KClass<A>): KAnnotationMirror? = this.getAnnotationsByType(clazz).firstOrNull()

    fun <A: Annotation> getAnnotationsByType(clazz: KClass<A>): Sequence<KAnnotationMirror> {
        return annotations.filter { it.annotationType.toString() == clazz.qualifiedName }
    }

    /**
     * Details of this annotated element
     */
    fun inspect(): List<String> {
        return this.annotations.toList().map {
            "@" + it.annotationType.toString() + if (it.fields.isNotEmpty()) {
                "(${it.fields.map { field ->
                    val valuePrefix = if (field.value.value is CharSequence) "\"" else ""
                    field.key.simpleName + ": " + field.key.asType().toString() +
                        if (field.value.value != null) " = $valuePrefix${field.value.value.toString()}$valuePrefix" else ""
                }.joinToString(separator = ", ", prefix = "", postfix = "")})"
            } else ""
        }
    }
}