package com.lovelycatv.vertex.lang.adapter.java.type

import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaTypeAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.type.KArrayType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.util.AbstractJavaAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import javax.lang.model.type.ArrayType

/**
 * @author lovelycat
 * @since 2025-06-03 22:13
 * @version 1.0
 */
class JavaArrayTypeAdapter(
    context: AbstractJavaAdapterContext
) : AbstractJavaTypeAdapter<ArrayType, KArrayType>(context) {
    override fun translate(type: ArrayType): KArrayType {
        return object : KArrayType {
            override val elementType: KTypeMirror
                get() = context.translateType(type.componentType)

            override fun toString(): String {
                return type.toString()
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = type.getKAnnotations(context)
        }
    }
}