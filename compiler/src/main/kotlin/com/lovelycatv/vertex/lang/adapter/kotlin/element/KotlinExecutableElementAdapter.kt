package com.lovelycatv.vertex.lang.adapter.kotlin.element

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.lovelycatv.vertex.lang.adapter.ActualKName
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinElementAdapter
import com.lovelycatv.vertex.lang.model.KName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.element.KExecutableElement
import com.lovelycatv.vertex.lang.model.element.KTypeParameterElement
import com.lovelycatv.vertex.lang.model.element.KVariableElement
import com.lovelycatv.vertex.lang.model.getPackageName
import com.lovelycatv.vertex.lang.model.platform.KotlinExecutableElement
import com.lovelycatv.vertex.lang.model.platform.KotlinExecutableType
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.model.type.KExecutableType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.model.type.KTypeVariable
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.modifier.KotlinModifier
import com.lovelycatv.vertex.lang.util.*

/**
 * @author lovelycat
 * @since 2025-06-23 16:09
 * @version 1.0
 */
class KotlinExecutableElementAdapter(
    context: IKotlinAdapterContext
) : AbstractKotlinElementAdapter<KSFunctionDeclaration, KExecutableElement>(context) {
    override fun translate(element: KSFunctionDeclaration): KotlinExecutableElement {
        return object : KotlinExecutableElement {
            override val original: Any
                get() = element
            override val isSuspend: Boolean
                get() = this.modifiers.any { it == KotlinModifier.SUSPEND }
            override val returnType: KTypeMirror
                get() = context.translateType(element.returnType?.resolve() ?: throw IllegalStateException("Return type should not be null"))
            override val parameters: List<KVariableElement<*>>
                get() = element.parameters.map { context.translateVariableElement(it) }
            override val throwTypes: List<KDeclaredType>
                get() = element.getThrowTypes(context).toList()
            override val name: KName
                get() = ActualKName(element.simpleName.asString(), null)
            override val typeParameters: List<KTypeParameterElement>
                get() = element.typeParameters.map { context.translateTypeParameterElement(it) }
            override val packageName: String
                get() = element.getPackageName()
            override val parentDeclaration: KElement<*>?
                get() = element.getParentKDeclaration(context)

            override fun asType(): KotlinExecutableType {
                val ref = this
                return object : KotlinExecutableType {
                    override val original: Any
                        get() = ref.original
                    override val isSuspend: Boolean
                        get() = ref.isSuspend
                    override val typeVariables: List<KTypeVariable>
                        get() = ref.typeParameters.map { it.asType() }
                    override val returnType: KTypeMirror
                        get() = ref.returnType
                    override val parameters: List<KTypeMirror>
                        get() = ref.parameters.map { it.asType() }
                    override val throwTypes: List<KDeclaredType>
                        get() = ref.throwTypes

                    override fun toString(): String {
                        return ref.toString()
                    }

                    override val annotations: Sequence<KAnnotationMirror>
                        get() = ref.annotations
                }
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = element.getKAnnotations(context)
            override val modifiers: Sequence<IModifier>
                get() = element.getKModifiers()

        }
    }
}