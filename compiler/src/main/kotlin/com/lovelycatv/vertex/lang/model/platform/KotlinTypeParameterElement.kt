package com.lovelycatv.vertex.lang.model.platform

import com.lovelycatv.vertex.lang.model.annotation.KAnnotated
import com.lovelycatv.vertex.lang.model.element.KTypeParameterElement

/**
 * @author lovelycat
 * @since 2025-06-26 04:57
 * @version 1.0
 */
interface KotlinTypeParameterElement : KTypeParameterElement {
    override val language: KAnnotated.Language
        get() = KAnnotated.Language.KOTLIN

    /**
     * It is impossible to infer the [com.google.devtools.ksp.symbol.KSType]
     * from a [com.google.devtools.ksp.symbol.KSTypeParameter]
     * due to the differences of Type between kotlin and java.
     *
     * The [KTypeParameterElement.original] will be this [KotlinTypeParameterElement]
     * instead of the corresponding [com.google.devtools.ksp.symbol.KSType].
     *
     * @return [KotlinTypeVariable]
     */
    override fun asType(): KotlinTypeVariable
}