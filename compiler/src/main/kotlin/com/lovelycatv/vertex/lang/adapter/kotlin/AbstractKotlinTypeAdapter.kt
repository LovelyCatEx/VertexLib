package com.lovelycatv.vertex.lang.adapter.kotlin

import com.google.devtools.ksp.symbol.*
import com.lovelycatv.vertex.lang.adapter.AbstractTypeAdapter
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext

/**
 * @author lovelycat
 * @since 2025-06-01 18:43
 * @version 1.0
 */
abstract class AbstractKotlinTypeAdapter<TARGET: KSType, R: KTypeMirror>(
    context: IKotlinAdapterContext
) : AbstractTypeAdapter<TARGET, R, KSAnnotation, KSAnnotated, KSType>(context)