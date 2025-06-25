package com.lovelycatv.vertex.lang.adapter.kotlin

import com.google.devtools.ksp.symbol.*
import com.lovelycatv.vertex.lang.adapter.AbstractElementAdapter
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext

/**
 * @author lovelycat
 * @since 2025-06-01 18:43
 * @version 1.0
 */
abstract class AbstractKotlinElementAdapter<TARGET: KSAnnotated, R: KElement<*>>(
    context: IKotlinAdapterContext
) : AbstractElementAdapter<TARGET, R, KSAnnotation, KSAnnotated, KSType>(context)