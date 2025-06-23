package com.lovelycatv.vertex.lang.adapter.java.type

import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaTypeAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.util.AbstractJavaAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import com.lovelycatv.vertex.lang.util.getParentKType
import javax.lang.model.type.DeclaredType

/**
 * @author lovelycat
 * @since 2025-06-03 21:59
 * @version 1.0
 */
class JavaDeclaredTypeAdapter(
    context: AbstractJavaAdapterContext
) : AbstractJavaTypeAdapter<DeclaredType, KDeclaredType>(context) {
    override fun translate(type: DeclaredType): KDeclaredType {
        return object : KDeclaredType {
            override val parentDeclaredType: KDeclaredType?
                get() = type.getParentKType(context)
            override val typeArguments: List<KTypeMirror>
                get() = type.typeArguments.map { context.translateType(it) }

            override fun toString(): String {
                return type.toString()
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = type.getKAnnotations(context)
        }
    }
}