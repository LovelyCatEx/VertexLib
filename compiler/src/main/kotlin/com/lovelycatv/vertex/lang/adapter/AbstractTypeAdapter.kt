package com.lovelycatv.vertex.lang.adapter

import com.lovelycatv.vertex.lang.model.type.KTypeMirror

/**
 * @author lovelycat
 * @since 2025-06-01 18:38
 * @version 1.0
 */
abstract class AbstractTypeAdapter<TARGET: Any, R: KTypeMirror, A: Any, E: Any, T: Any>(
    context: AbstractAdapterContext<A, E, T>
): AbstractAdapter<A, E, T>(context) {
    abstract fun translate(type: TARGET): R
}