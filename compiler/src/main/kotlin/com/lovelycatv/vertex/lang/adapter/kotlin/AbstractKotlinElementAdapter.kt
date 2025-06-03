package com.lovelycatv.vertex.lang.adapter.kotlin

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.lovelycatv.vertex.lang.adapter.AbstractElementAdapter
import com.lovelycatv.vertex.lang.model.element.KElement

/**
 * @author lovelycat
 * @since 2025-06-01 18:43
 * @version 1.0
 */
abstract class AbstractKotlinElementAdapter<TARGET: KSDeclaration, R: KElement<*>>(
    context: KotlinAdapterContext
) : AbstractElementAdapter<TARGET, R, KSAnnotation, KSDeclaration, KSType>(context)