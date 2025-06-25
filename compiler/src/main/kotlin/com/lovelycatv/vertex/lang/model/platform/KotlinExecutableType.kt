package com.lovelycatv.vertex.lang.model.platform

import com.lovelycatv.vertex.lang.model.element.KExecutableElement
import com.lovelycatv.vertex.lang.model.type.KExecutableType

/**
 * @author lovelycat
 * @since 2025-06-26 04:49
 * @version 1.0
 */
interface KotlinExecutableType : KExecutableType {
    /**
     * It is impossible to infer the KSType from a KSFunctionDeclaration
     * due to the differences of Type between kotlin and java.
     *
     * If this [KExecutableType] is translated from a
     * [com.google.devtools.ksp.symbol.KSType] with [com.google.devtools.ksp.symbol.KSFunctionDeclaration],
     * then the value of this variable is the [com.google.devtools.ksp.symbol.KSType].
     *
     * Otherwise the value of this variable will be a [KotlinExecutableElement].
     *
     * @return [com.google.devtools.ksp.symbol.KSType] or [KotlinExecutableElement]
     */
    override val original: Any

    val isSuspend: Boolean
}