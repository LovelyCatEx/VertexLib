package com.lovelycatv.vertex.lang.adapter.kotlin

import com.google.devtools.ksp.symbol.*
import com.lovelycatv.vertex.lang.adapter.AbstractAnnotationAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext

/**
 * @author lovelycat
 * @since 2025-06-01 18:43
 * @version 1.0
 */
abstract class AbstractKotlinAnnotationAdapter<TARGET: KSAnnotation, R: KAnnotationMirror>(
    context: IKotlinAdapterContext
) : AbstractAnnotationAdapter<TARGET, R, KSAnnotation, KSAnnotated, KSType>(context)