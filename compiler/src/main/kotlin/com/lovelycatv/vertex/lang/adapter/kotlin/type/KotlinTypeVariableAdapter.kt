package com.lovelycatv.vertex.lang.adapter.kotlin.type

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinTypeAdapter
import com.lovelycatv.vertex.lang.adapter.kotlin.element.KotlinTypeParameterAdapter
import com.lovelycatv.vertex.lang.model.type.KTypeVariable
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext

/**
 * @author lovelycat
 * @since 2025-06-24 00:16
 * @version 1.0
 */
class KotlinTypeVariableAdapter(
    context: IKotlinAdapterContext
) : AbstractKotlinTypeAdapter<KSType, KTypeVariable>(context) {
    override fun translate(type: KSType): KTypeVariable {
        val declaration = type.declaration
        if (declaration !is KSTypeParameter) {
            throw IllegalArgumentException("${type.declaration::class.qualifiedName} can not be cast to KSTypeParameter")
        }

        return KotlinTypeParameterAdapter(context).translate(declaration).asType()
    }
}