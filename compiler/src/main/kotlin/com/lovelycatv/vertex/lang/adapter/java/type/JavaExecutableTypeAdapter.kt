package com.lovelycatv.vertex.lang.adapter.java.type

import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaTypeAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.model.type.KExecutableType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.model.type.KTypeVariable
import com.lovelycatv.vertex.lang.util.IJavaAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.ExecutableType

/**
 * @author lovelycat
 * @since 2025-06-03 22:47
 * @version 1.0
 */
class JavaExecutableTypeAdapter(
    context: IJavaAdapterContext
) : AbstractJavaTypeAdapter<ExecutableType, KExecutableType>(context) {
    override fun translate(type: ExecutableType): KExecutableType {
        return object : KExecutableType {
            override val original: Any
                get() = type
            override val typeVariables: List<KTypeVariable>
                get() = type.typeVariables.map { context.translateTypeVariable(it) }
            override val returnType: KTypeMirror
                get() = context.translateType(type.returnType)
            override val parameters: List<KTypeMirror>
                get() = type.parameterTypes.map { context.translateType(it) }
            override val throwTypes: List<KDeclaredType>
                get() = type.thrownTypes.filterIsInstance<DeclaredType>().map { context.translateDeclaredType(it) }

            override fun toString(): String {
                return type.toString()
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = type.getKAnnotations(context)
        }
    }
}