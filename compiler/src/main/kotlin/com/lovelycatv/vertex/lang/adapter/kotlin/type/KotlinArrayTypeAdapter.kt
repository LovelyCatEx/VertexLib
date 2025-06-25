package com.lovelycatv.vertex.lang.adapter.kotlin.type

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Variance
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinTypeAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.findTopLevelAnyType
import com.lovelycatv.vertex.lang.model.isArrayType
import com.lovelycatv.vertex.lang.model.type.KArrayType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext
import com.lovelycatv.vertex.lang.util.toKAnnotations

/**
 * @author lovelycat
 * @since 2025-06-24 02:36
 * @version 1.0
 */
class KotlinArrayTypeAdapter(
    context: IKotlinAdapterContext
) : AbstractKotlinTypeAdapter<KSType, KArrayType>(context) {
    override fun translate(type: KSType): KArrayType {
        if (!type.isArrayType()) {
            throw IllegalArgumentException("This type is not a valid array type")
        }

        return object : KArrayType {
            override val elementType: KTypeMirror
                get() = context.translateType(type.arguments[0].run {
                    if (this.type != null) {
                        this.type!!.resolve()
                    } else if (this.variance == Variance.STAR) {
                        (type.declaration as KSClassDeclaration).findTopLevelAnyType()
                    } else {
                        throw IllegalStateException("Could not recognize the component type of array: $type")
                    }
                })

            override fun toString(): String {
                return type.toString()
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = type.annotations.toKAnnotations(context)

        }
    }
}