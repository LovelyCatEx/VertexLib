package com.lovelycatv.vertex.lang.adapter.java.element

import com.lovelycatv.vertex.lang.adapter.ActualKName
import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaElementAdapter
import com.lovelycatv.vertex.lang.model.KName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotated
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KDeclaredTypeElement
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.element.KTypeParameterElement
import com.lovelycatv.vertex.lang.model.getPackageName
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.util.*
import javax.lang.model.element.TypeElement
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.type.DeclaredType

/**
 * @author lovelycat
 * @since 2025-06-01 18:42
 * @version 1.0
 */
class JavaTypeElementAdapter(
    context: IJavaAdapterContext
) : AbstractJavaElementAdapter<TypeElement, KDeclaredTypeElement>(context) {
    override fun translate(element: TypeElement): KDeclaredTypeElement {
        return object : KDeclaredTypeElement {
            override val original: Any
                get() = element
            override val language: KAnnotated.Language
                get() = KAnnotated.Language.JAVA
            override val superClass: KDeclaredType
                get() = context.translateDeclaredType(element.superclass as DeclaredType)
            override val interfaces: Sequence<KDeclaredType>
                get() = element.interfaces.map { context.translateDeclaredType(it as DeclaredType) }.asSequence()
            override val name: KName
                get() = ActualKName(element.simpleName.toString(), element.qualifiedName.toString())
            override val typeParameters: List<KTypeParameterElement>
                get() = element.typeParameters.map { context.translateTypeParameterElement(it as TypeParameterElement) }
            override val packageName: String
                get() = element.getPackageName()
            override val parentDeclaration: KElement<*>?
                get() = element.getParentKDeclaration(context)

            override fun asType(): KDeclaredType {
                return context.translateDeclaredType(element.asType() as DeclaredType)
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = element.getKAnnotations(context)

            override val modifiers: Sequence<IModifier>
                get() = element.getKModifiers()
            override val declarations: Sequence<KElement<*>>
                get() = element.getChildrenDeclarations(context)
        }
    }
}