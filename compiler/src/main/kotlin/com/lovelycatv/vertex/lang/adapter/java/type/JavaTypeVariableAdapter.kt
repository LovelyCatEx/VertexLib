package com.lovelycatv.vertex.lang.adapter.java.type

import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaTypeAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KTypeParameterElement
import com.lovelycatv.vertex.lang.model.type.KReferenceType
import com.lovelycatv.vertex.lang.model.type.KTypeVariable
import com.lovelycatv.vertex.lang.util.IJavaAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.IntersectionType
import javax.lang.model.type.TypeVariable

/**
 * @author lovelycat
 * @since 2025-06-03 22:15
 * @version 1.0
 */
class JavaTypeVariableAdapter(
    context: IJavaAdapterContext
) : AbstractJavaTypeAdapter<TypeVariable, KTypeVariable>(context) {
    override fun translate(type: TypeVariable): KTypeVariable {
        return object : KTypeVariable {
            override val original: Any
                get() = type
            override val upperBounds: Sequence<KReferenceType>
                get() = type.upperBound.run {
                    when (this) {
                        is DeclaredType -> {
                            if (this.toString() == "java.lang.Object") {
                                sequenceOf(context.translateDeclaredType(this))
                            } else {
                                sequenceOf(context.translateDeclaredType(this))
                            }
                        }
                        is IntersectionType ->
                            this.bounds.map { context.translateType(it) }
                            .filterIsInstance<KReferenceType>()
                            .asSequence()
                        else -> throw IllegalStateException("Unsupported type variable bound type: ${this::class.qualifiedName}")
                    }
                }

            override fun asElement(): KTypeParameterElement {
                return context.translateTypeParameterElement(type.asElement())
            }

            override fun toString(): String {
                return type.toString()
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = type.getKAnnotations(context)
        }
    }
}