package com.lovelycatv.vertex.lang.adapter.kotlin.type

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Variance
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinTypeAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KDeclaredTypeElement
import com.lovelycatv.vertex.lang.model.findTopLevelAnyType
import com.lovelycatv.vertex.lang.model.type.*
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import com.lovelycatv.vertex.lang.util.toKAnnotations

/**
 * @author lovelycat
 * @since 2025-06-24 00:16
 * @version 1.0
 */
class KotlinDeclaredTypeAdapter(
    context: IKotlinAdapterContext
) : AbstractKotlinTypeAdapter<KSType, KDeclaredType>(context) {
    override fun translate(type: KSType): KDeclaredType {
        val declaration = type.declaration
        if (declaration !is KSClassDeclaration) {
            throw IllegalArgumentException("${type.declaration::class.qualifiedName} can not be cast to KSClassDeclaration")
        }

        return object : KDeclaredType {
            override val original: Any
                get() = type
            override val parentDeclaredType: KDeclaredType?
                get() = declaration.parentDeclaration?.let {
                    context.translateTypeElement(it as KSClassDeclaration).asType()
                }
            override val typeArguments: List<KTypeMirror>
                get() = type.arguments.map {
                    val typeMirror = if (it.type != null) {
                        context.translateType(it.type!!.resolve())
                    } else {
                        context.translateType(declaration.findTopLevelAnyType())
                    }

                    when (it.variance) {
                        Variance.INVARIANT -> {
                            // Represents a invariant type, such as Array<String>
                            typeMirror
                        }
                        Variance.STAR -> {
                            // Represents a star type, such as Array<*>
                            typeMirror
                        }
                        Variance.COVARIANT, Variance.CONTRAVARIANT -> {
                            object : KWildcardType {
                                override val original: Any
                                    get() = it
                                override val extendsBound: KTypeMirror?
                                    get() = if (it.variance == Variance.COVARIANT) typeMirror else null
                                override val superBound: KTypeMirror?
                                    get() = if (it.variance == Variance.CONTRAVARIANT) typeMirror else null

                                override fun toString(): String {
                                    return it.toString()
                                }

                                override val annotations: Sequence<KAnnotationMirror>
                                    get() = it.getKAnnotations(context)


                            }
                        }
                        else -> throw IllegalArgumentException("")
                    }
                }

            override fun asElement(): KDeclaredTypeElement {
                return context.translateTypeElement(type.declaration)
            }

            override fun toString(): String {
                return declaration.qualifiedName?.asString() ?: declaration.toString()
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = type.annotations.toKAnnotations(context)

        }
    }
}