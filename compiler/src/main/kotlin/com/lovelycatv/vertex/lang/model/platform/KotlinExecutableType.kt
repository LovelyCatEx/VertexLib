package com.lovelycatv.vertex.lang.model.platform

import com.lovelycatv.vertex.lang.model.annotation.KAnnotated
import com.lovelycatv.vertex.lang.model.type.KExecutableType

/**
 * @author lovelycat
 * @since 2025-06-26 04:49
 * @version 1.0
 */
interface KotlinExecutableType : KExecutableType {
    /**
     * If this [KExecutableType] is translated from a
     * [com.google.devtools.ksp.symbol.KSType] with [com.google.devtools.ksp.symbol.KSFunctionDeclaration],
     * then the value of this variable is the [com.google.devtools.ksp.symbol.KSType].
     *
     * Otherwise the value of this variable will be a [KotlinExecutableElement].
     *
     * @return [com.google.devtools.ksp.symbol.KSType] or [KotlinExecutableElement]
     */
    override val original: Any

    override val language: KAnnotated.Language
        get() = KAnnotated.Language.KOTLIN

    val isSuspend: Boolean
}