package com.lovelycatv.vertex.lang.model.platform

import com.lovelycatv.vertex.lang.model.annotation.KAnnotated
import com.lovelycatv.vertex.lang.model.type.KTypeVariable

/**
 * @author lovelycat
 * @since 2025-06-26 04:58
 * @version 1.0
 */
interface KotlinTypeVariable : KTypeVariable {
    /**
     * It is impossible to infer the KSType from a KSFunctionDeclaration
     * due to the differences of Type between kotlin and java.
     *
     * If this [KTypeVariable] is translated from a
     * [com.google.devtools.ksp.symbol.KSType] with [com.google.devtools.ksp.symbol.KSTypeParameter],
     * then the value of this variable is the [com.google.devtools.ksp.symbol.KSType].
     *
     * Otherwise the value of this variable will be a [KotlinTypeParameterElement].
     *
     * @return [com.google.devtools.ksp.symbol.KSType] or [KotlinTypeParameterElement]
     */
    override val original: Any

    override val language: KAnnotated.Language
        get() = KAnnotated.Language.KOTLIN
}