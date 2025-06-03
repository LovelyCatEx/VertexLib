package com.lovelycatv.vertex.lang.model.annotation

import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-05-29 23:20
 * @version 1.0
 */
interface KAnnotationContainer {
    val annotations: Sequence<KAnnotationMirror>

    fun <A: Annotation> getAnnotationByType(clazz: KClass<A>): KAnnotationMirror? = this.getAnnotationsByType(clazz).firstOrNull()

    fun <A: Annotation> getAnnotationsByType(clazz: KClass<A>): Sequence<KAnnotationMirror> {
        return annotations.filter { it.annotationType.toString() == clazz.qualifiedName }
    }
}