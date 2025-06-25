package com.lovelycatv.vertex.lang.adapter.java.element

import com.lovelycatv.vertex.lang.adapter.ActualKName
import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaElementAdapter
import com.lovelycatv.vertex.lang.model.KName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.element.KTypeParameterElement
import com.lovelycatv.vertex.lang.model.getPackageName
import com.lovelycatv.vertex.lang.model.type.KTypeVariable
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.util.IJavaAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import com.lovelycatv.vertex.lang.util.getKModifiers
import com.lovelycatv.vertex.lang.util.getParentKDeclaration
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.type.TypeVariable

/**
 * @author lovelycat
 * @since 2025-06-01 19:30
 * @version 1.0
 */
class JavaTypeParameterElementAdapter(
    context: IJavaAdapterContext
) : AbstractJavaElementAdapter<TypeParameterElement, KTypeParameterElement>(context) {
    override fun translate(element: TypeParameterElement): KTypeParameterElement {
        return object : KTypeParameterElement {
            override val original: Any
                get() = element
            override val genericElement: KElement<*>
                get() = element.genericElement.run {
                    when (this) {
                        is TypeElement -> {
                            context.translateTypeElement(this)
                        }

                        is ExecutableElement -> {
                            context.translateExecutableElement(this)
                        }

                        is TypeParameterElement -> {
                            context.translateTypeParameterElement(this)
                        }

                        else -> throw IllegalStateException("Unrecognized generic element type: ${this::class.qualifiedName}")
                    }
                }
            override val modifiers: Sequence<IModifier>
                get() = element.getKModifiers()
            override val name: KName
                get() = ActualKName(element.simpleName.toString(), null)
            override val packageName: String
                get() = element.getPackageName()
            override val parentDeclaration: KElement<*>?
                get() = element.getParentKDeclaration(context)

            override fun asType(): KTypeVariable {
                return context.translateTypeVariable(element.asType() as TypeVariable)
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = element.getKAnnotations(context)
        }
    }
}