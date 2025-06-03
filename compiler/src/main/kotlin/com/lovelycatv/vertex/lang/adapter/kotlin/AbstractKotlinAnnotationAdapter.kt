package com.lovelycatv.vertex.lang.adapter.kotlin

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.lovelycatv.vertex.lang.adapter.AbstractAnnotationAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror

/**
 * @author lovelycat
 * @since 2025-06-01 18:43
 * @version 1.0
 */
abstract class AbstractKotlinAnnotationAdapter<TARGET: Annotation, R: KAnnotationMirror>(
    context: KotlinAdapterContext
) : AbstractAnnotationAdapter<TARGET, R, KSAnnotation, KSDeclaration, KSType>(context)