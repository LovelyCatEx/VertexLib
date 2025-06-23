package com.lovelycatv.vertex.lang.adapter.kotlin.element

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.lovelycatv.vertex.lang.adapter.ActualKName
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinElementAdapter
import com.lovelycatv.vertex.lang.model.KName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.element.KVariableElement
import com.lovelycatv.vertex.lang.model.getPackageName
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.modifier.KotlinModifier
import com.lovelycatv.vertex.lang.util.AbstractKotlinAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations

/**
 * @author lovelycat
 * @since 2025-06-23 16:09
 * @version 1.0
 */
class KotlinVariableElementAdapterForValueParameter(
    context: AbstractKotlinAdapterContext
) : AbstractKotlinElementAdapter<KSValueParameter, KVariableElement<KTypeMirror>>(context) {
    override fun translate(element: KSValueParameter): KVariableElement<KTypeMirror> {
        return object : KVariableElement<KTypeMirror> {
            override fun asType(): KTypeMirror {
                return context.translateType(element.type.resolve())
            }

            override val constantValue: Any?
                get() = null
            override val name: KName
                get() = ActualKName(element.name?.asString() ?: "", null)
            override val packageName: String
                get() = element.getPackageName()
            override val parentDeclaration: KElement<*>?
                get() = element.parent?.let {
                    if (it is KSFunctionDeclaration) {
                        context.translateExecutableElement(it)
                    } else null
                }
            override val annotations: Sequence<KAnnotationMirror>
                get() = element.getKAnnotations(context)
            override val modifiers: Sequence<IModifier>
                get() = mutableListOf<IModifier>().apply {
                    if (element.isVal) {
                        this.add(KotlinModifier.VAL)
                    }
                    if (element.isVar) {
                        this.add(KotlinModifier.VAR)
                    }
                    if (element.isVararg) {
                        this.add(KotlinModifier.VARARG)
                    }
                    if (element.isCrossInline) {
                        this.add(KotlinModifier.CROSS_INLINE)
                    }
                    if (element.isNoInline) {
                        this.add(KotlinModifier.NO_INLINE)
                    }
                }.asSequence()

        }
    }
}