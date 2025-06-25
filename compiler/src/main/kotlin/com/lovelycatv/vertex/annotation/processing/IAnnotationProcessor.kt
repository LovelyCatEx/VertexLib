package com.lovelycatv.vertex.annotation.processing

import com.lovelycatv.vertex.lang.adapter.IAdapterContext
import com.lovelycatv.vertex.lang.model.annotation.KAnnotated
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-05-30 15:05
 * @version 1.0
 */
interface IAnnotationProcessor {
    fun getSupportedAnnotations(): Set<KClass<out Annotation>>

    fun process(map: Map<KClass<out Annotation>, List<KAnnotated>>): Boolean

    fun getAdapterContext(): IAdapterContext<*, *, *>
}