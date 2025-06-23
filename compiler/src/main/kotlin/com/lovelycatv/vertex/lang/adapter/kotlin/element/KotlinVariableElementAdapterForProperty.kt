package com.lovelycatv.vertex.lang.adapter.kotlin.element

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.lovelycatv.vertex.lang.adapter.ActualKName
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinElementAdapter
import com.lovelycatv.vertex.lang.model.KName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.element.KVariableElement
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.util.AbstractKotlinAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import com.lovelycatv.vertex.lang.util.getKModifiers

/**
 * @author lovelycat
 * @since 2025-06-23 16:09
 * @version 1.0
 */
class KotlinVariableElementAdapterForProperty(
    context: AbstractKotlinAdapterContext
) : AbstractKotlinElementAdapter<KSPropertyDeclaration, KVariableElement<KTypeMirror>>(context) {
    override fun translate(element: KSPropertyDeclaration): KVariableElement<KTypeMirror> {
        return object : KVariableElement<KTypeMirror> {
            override fun asType(): KTypeMirror {
                return context.translateType(element.type.resolve())
            }

            override val constantValue: Any?
                get() = null
            override val name: KName
                get() = ActualKName(element.simpleName.asString(), element.packageName.asString())
            override val packageName: String
                get() = element.packageName.asString()
            override val parentDeclaration: KElement<*>?
                get() = element.parentDeclaration?.let { context.translateElement(it)  }
            override val annotations: Sequence<KAnnotationMirror>
                get() = element.getKAnnotations(context)
            override val modifiers: Sequence<IModifier>
                get() = element.getKModifiers()

        }
    }
}