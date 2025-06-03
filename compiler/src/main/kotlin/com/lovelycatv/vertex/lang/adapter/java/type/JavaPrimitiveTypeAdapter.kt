package com.lovelycatv.vertex.lang.adapter.java.type

import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaTypeAdapter
import com.lovelycatv.vertex.lang.adapter.java.JavaAdapterContext
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.type.KPrimitiveType
import com.lovelycatv.vertex.lang.util.getKAnnotations
import javax.lang.model.type.PrimitiveType

/**
 * @author lovelycat
 * @since 2025-06-03 22:13
 * @version 1.0
 */
class JavaPrimitiveTypeAdapter(
    context: JavaAdapterContext
) : AbstractJavaTypeAdapter<PrimitiveType, KPrimitiveType>(context) {
    override fun translate(type: PrimitiveType): KPrimitiveType {
        return object : KPrimitiveType {
            override fun toString(): String {
                return type.toString()
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = type.getKAnnotations(context)
        }
    }
}