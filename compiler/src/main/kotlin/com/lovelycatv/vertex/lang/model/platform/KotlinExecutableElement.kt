package com.lovelycatv.vertex.lang.model.platform

import com.lovelycatv.vertex.lang.model.element.KExecutableElement
import com.lovelycatv.vertex.lang.model.type.KExecutableType

/**
 * @author lovelycat
 * @since 2025-06-26 03:53
 * @version 1.0
 */
interface KotlinExecutableElement : KExecutableElement {
    val isSuspend: Boolean

    /**
     * It is impossible to infer the [com.google.devtools.ksp.symbol.KSType]
     *  from a [com.google.devtools.ksp.symbol.KSFunctionDeclaration]
     * due to the differences of Type between kotlin and java.
     *
     * The [KExecutableType.original] will be this [KotlinExecutableElement]
     * instead of the corresponding [com.google.devtools.ksp.symbol.KSType].
     *
     * @return [KotlinExecutableType]
     */
    override fun asType(): KotlinExecutableType
}