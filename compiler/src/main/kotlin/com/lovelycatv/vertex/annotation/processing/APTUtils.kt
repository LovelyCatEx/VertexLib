package com.lovelycatv.vertex.annotation.processing

import javax.lang.model.element.*
import javax.lang.model.type.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * @author lovelycat
 * @since 2025-05-26 22:27
 * @version 1.0
 */
class APTUtils private constructor()

fun AnnotationMirror.compareType(annotationClazz: Class<out Annotation>): Boolean {
    return this.annotationType.toString() == annotationClazz.name
}