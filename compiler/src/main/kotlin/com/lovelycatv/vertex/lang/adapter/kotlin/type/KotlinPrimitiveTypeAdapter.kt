package com.lovelycatv.vertex.lang.adapter.kotlin.type

import com.google.devtools.ksp.symbol.KSType
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinTypeAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.isPrimitiveType
import com.lovelycatv.vertex.lang.model.type.KPrimitiveType
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext
import com.lovelycatv.vertex.lang.util.toKAnnotations

/**
 * @author lovelycat
 * @since 2025-06-24 03:11
 * @version 1.0
 */
class KotlinPrimitiveTypeAdapter(
    context: IKotlinAdapterContext
) : AbstractKotlinTypeAdapter<KSType, KPrimitiveType>(context) {
    override fun translate(type: KSType): KPrimitiveType {
        if (!type.isPrimitiveType()) {
            throw IllegalArgumentException("This type is not a valid primitive type")
        }


        return object : KPrimitiveType {
            override val original: Any
                get() = type

            override fun toString(): String {
                return type.toString()
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = type.annotations.toKAnnotations(context)
        }
    }
}