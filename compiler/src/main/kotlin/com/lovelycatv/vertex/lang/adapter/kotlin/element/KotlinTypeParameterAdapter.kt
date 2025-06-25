package com.lovelycatv.vertex.lang.adapter.kotlin.element

import com.google.devtools.ksp.symbol.KSTypeParameter
import com.lovelycatv.vertex.lang.adapter.ActualKName
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinElementAdapter
import com.lovelycatv.vertex.lang.model.KName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.element.KTypeParameterElement
import com.lovelycatv.vertex.lang.model.type.KReferenceType
import com.lovelycatv.vertex.lang.model.type.KTypeVariable
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import com.lovelycatv.vertex.lang.util.getKModifiers
import com.lovelycatv.vertex.lang.util.getParentKDeclaration

/**
 * @author lovelycat
 * @since 2025-06-23 16:09
 * @version 1.0
 */
class KotlinTypeParameterAdapter(
    context: IKotlinAdapterContext
) : AbstractKotlinElementAdapter<KSTypeParameter, KTypeParameterElement>(context) {
    override fun translate(element: KSTypeParameter): KTypeParameterElement {
        return object : KTypeParameterElement {
            override val genericElement: KElement<*>
                get() = context.translateElement(element.parentDeclaration ?: throw IllegalStateException(""))
            override val modifiers: Sequence<IModifier>
                get() = element.getKModifiers()
            override val name: KName
                get() = ActualKName(element.simpleName.asString(), element.qualifiedName?.asString())
            override val packageName: String
                get() = element.packageName.asString()
            override val parentDeclaration: KElement<*>?
                get() = element.getParentKDeclaration(context)

            override fun asType(): KTypeVariable {
                return object : KTypeVariable {
                    override val upperBounds: Sequence<KReferenceType>
                        get() = element.bounds.map { context.translateType(it.resolve()) }.filterIsInstance<KReferenceType>()

                    override fun asElement(): KTypeParameterElement {
                        return context.translateTypeParameterElement(element)
                    }

                    override fun toString(): String {
                        return element.name.asString()
                    }

                    override val annotations: Sequence<KAnnotationMirror>
                        get() = element.getKAnnotations(context)

                }
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = element.getKAnnotations(context)

        }
    }
}