package com.lovelycatv.vertex.lang.adapter.kotlin.element

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.lovelycatv.vertex.lang.adapter.ActualKName
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinElementAdapter
import com.lovelycatv.vertex.lang.model.KName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KDeclaredTypeElement
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.element.KTypeParameterElement
import com.lovelycatv.vertex.lang.model.getInterfaces
import com.lovelycatv.vertex.lang.model.getSuperClass
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import com.lovelycatv.vertex.lang.util.getKModifiers

/**
 * @author lovelycat
 * @since 2025-06-23 16:09
 * @version 1.0
 */
class KotlinTypeElementAdapter(
    context: IKotlinAdapterContext
) : AbstractKotlinElementAdapter<KSClassDeclaration, KDeclaredTypeElement>(
    context
) {
    override fun translate(element: KSClassDeclaration): KDeclaredTypeElement {
        return object : KDeclaredTypeElement {
            override val superClass: KDeclaredType
                get() = context.translateDeclaredType(element.getSuperClass().resolve())
            override val interfaces: Sequence<KDeclaredType>
                get() = element.getInterfaces().map { context.translateDeclaredType(it.resolve()) }
            override val name: KName
                get() = ActualKName(element.simpleName.asString(), element.qualifiedName?.asString())
            override val typeParameters: List<KTypeParameterElement>
                get() = element.typeParameters.map { context.translateTypeParameterElement(it) }
            override val packageName: String
                get() = element.packageName.asString()
            override val parentDeclaration: KElement<*>?
                get() = element.parentDeclaration?.let { context.translateElement(it) }

            override fun asType(): KDeclaredType {
                return context.translateDeclaredType(element.asStarProjectedType())
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = element.getKAnnotations(context)
            override val modifiers: Sequence<IModifier>
                get() = element.getKModifiers()
            override val declarations: Sequence<KElement<*>>
                get() = element.declarations.map { context.translateElement(it) }

        }
    }
}