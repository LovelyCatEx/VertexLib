package com.lovelycatv.vertex.lang.adapter

import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror

/**
 * @author lovelycat
 * @since 2025-06-01 18:38
 * @version 1.0
 */
abstract class AbstractAnnotationAdapter<TARGET: Any, R: KAnnotationMirror, A: Any, E: Any, T: Any>(
    context: AbstractAdapterContext<A, E, T>
) : AbstractAdapter<A, E, T>(context) {
    abstract fun translate(annotation: TARGET): R
}