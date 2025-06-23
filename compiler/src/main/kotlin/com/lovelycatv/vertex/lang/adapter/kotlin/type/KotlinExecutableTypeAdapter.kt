package com.lovelycatv.vertex.lang.adapter.kotlin.type

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinTypeAdapter
import com.lovelycatv.vertex.lang.adapter.kotlin.element.KotlinExecutableElementAdapter
import com.lovelycatv.vertex.lang.model.type.KExecutableType
import com.lovelycatv.vertex.lang.util.AbstractKotlinAdapterContext

/**
 * @author lovelycat
 * @since 2025-06-24 00:16
 * @version 1.0
 */
class KotlinExecutableTypeAdapter(
    context: AbstractKotlinAdapterContext
) : AbstractKotlinTypeAdapter<KSType, KExecutableType>(context) {
    override fun translate(type: KSType): KExecutableType {
        val declaration = type.declaration
        if (declaration !is KSFunctionDeclaration) {
            throw IllegalArgumentException("${type.declaration::class.qualifiedName} can not be cast to KSFunctionDeclaration")
        }

        return KotlinExecutableElementAdapter(context).translate(declaration).asType()
    }
}