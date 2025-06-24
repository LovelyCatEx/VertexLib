package com.lovelycatv.vertex.annotation.processing.annotations

@Target(AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class TypeParameterAnnotation(
    val alias: String
)
