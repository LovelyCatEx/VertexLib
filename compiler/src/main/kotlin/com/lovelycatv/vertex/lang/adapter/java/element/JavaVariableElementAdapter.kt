package com.lovelycatv.vertex.lang.adapter.java.element

import com.lovelycatv.vertex.lang.adapter.ActualKName
import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaElementAdapter
import com.lovelycatv.vertex.lang.model.KName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotated
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.element.KVariableElement
import com.lovelycatv.vertex.lang.model.getPackageName
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.util.IJavaAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import com.lovelycatv.vertex.lang.util.getKModifiers
import com.lovelycatv.vertex.lang.util.getParentKDeclaration
import javax.lang.model.element.VariableElement

/**
 * @author lovelycat
 * @since 2025-06-03 21:48
 * @version 1.0
 */
class JavaVariableElementAdapter(
    context: IJavaAdapterContext
) : AbstractJavaElementAdapter<VariableElement, KVariableElement<KTypeMirror>>(context) {
    override fun translate(element: VariableElement): KVariableElement<KTypeMirror> {
        return object : KVariableElement<KTypeMirror> {
            override val original: Any
                get() = element
            override val language: KAnnotated.Language
                get() = KAnnotated.Language.JAVA
            override fun asType(): KTypeMirror {
                return context.translateType(element.asType())
            }

            override val constantValue: Any?
                get() = element.constantValue
            override val name: KName
                get() = ActualKName(element.simpleName.toString(), null)
            override val packageName: String
                get() = element.getPackageName()
            override val parentDeclaration: KElement<*>?
                get() = element.getParentKDeclaration(context)
            override val annotations: Sequence<KAnnotationMirror>
                get() = element.getKAnnotations(context)

            override val modifiers: Sequence<IModifier>
                get() = element.getKModifiers()
        }
    }
}