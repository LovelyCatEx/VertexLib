package com.lovelycatv.vertex.lang.adapter.java.type

import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaTypeAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.model.type.KWildcardType
import com.lovelycatv.vertex.lang.util.IJavaAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import javax.lang.model.type.WildcardType

/**
 * @author lovelycat
 * @since 2025-06-03 23:23
 * @version 1.0
 */
class JavaWildcardTypeAdapter(
    context: IJavaAdapterContext
) : AbstractJavaTypeAdapter<WildcardType, KWildcardType>(context) {
    override fun translate(type: WildcardType): KWildcardType {
        return object : KWildcardType {
            override val extendsBound: KTypeMirror?
                get() = type.extendsBound?.run {
                    context.translateType(this)
                }
            override val superBound: KTypeMirror?
                get() = type.superBound?.run {
                    context.translateType(this)
                }

            override fun toString(): String {
                return type.toString()
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = type.getKAnnotations(context )
        }
    }
}