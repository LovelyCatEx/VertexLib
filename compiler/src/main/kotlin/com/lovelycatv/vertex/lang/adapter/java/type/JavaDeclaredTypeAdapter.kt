package com.lovelycatv.vertex.lang.adapter.java.type

import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaTypeAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotated
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KDeclaredTypeElement
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.util.IJavaAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import com.lovelycatv.vertex.lang.util.getParentKType
import javax.lang.model.type.DeclaredType

/**
 * @author lovelycat
 * @since 2025-06-03 21:59
 * @version 1.0
 */
class JavaDeclaredTypeAdapter(
    context: IJavaAdapterContext
) : AbstractJavaTypeAdapter<DeclaredType, KDeclaredType>(context) {
    override fun translate(type: DeclaredType): KDeclaredType {
        return object : KDeclaredType {
            override val original: Any
                get() = type
            override val language: KAnnotated.Language
                get() = KAnnotated.Language.JAVA
            override val parentDeclaredType: KDeclaredType?
                get() = type.getParentKType(context)
            override val typeArguments: List<KTypeMirror>
                get() = type.typeArguments.map { context.translateType(it) }

            override fun asElement(): KDeclaredTypeElement {
                return context.translateTypeElement(type.asElement())
            }

            override fun toString(): String {
                return type.toString()
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = type.getKAnnotations(context)
        }
    }
}