package com.lovelycatv.vertex.annotation.processing.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class TestAnnotation(
    val name: String,
    val classArray: Array<KClass<*>> = [],
    val clazz: KClass<*>
)