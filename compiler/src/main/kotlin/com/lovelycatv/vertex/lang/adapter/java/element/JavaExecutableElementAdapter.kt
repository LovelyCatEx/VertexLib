package com.lovelycatv.vertex.lang.adapter.java.element

import com.lovelycatv.vertex.lang.adapter.ActualKName
import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaElementAdapter
import com.lovelycatv.vertex.lang.adapter.java.JavaAdapterContext
import com.lovelycatv.vertex.lang.model.KName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.element.KExecutableElement
import com.lovelycatv.vertex.lang.model.element.KTypeParameterElement
import com.lovelycatv.vertex.lang.model.element.KVariableElement
import com.lovelycatv.vertex.lang.model.getPackageName
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.model.type.KExecutableType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.model.type.KTypeVariable
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.util.getKAnnotations
import com.lovelycatv.vertex.lang.util.getKModifiers
import com.lovelycatv.vertex.lang.util.getParentKDeclaration
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.type.DeclaredType

/**
 * @author lovelycat
 * @since 2025-06-03 21:48
 * @version 1.0
 */
class JavaExecutableElementAdapter(
    context: JavaAdapterContext
) : AbstractJavaElementAdapter<ExecutableElement, KExecutableElement>(context) {
    override fun translate(element: ExecutableElement): KExecutableElement {
        return object : KExecutableElement {
            override val typeVariables: List<KTypeVariable>
                get() = this.typeParameters.map { it.asType() }
            override val returnType: KTypeMirror
                get() = context.translateType(element.returnType)
            override val parameters: List<KVariableElement<*>>
                get() = element.parameters.map { context.translateVariableElement(it) }
            override val throwTypes: List<KDeclaredType>
                get() = element.thrownTypes.filterIsInstance<DeclaredType>().map { context.translateDeclaredType(it) }
            override val name: KName
                get() = ActualKName(element.simpleName.toString(), null)
            override val typeParameters: List<KTypeParameterElement>
                get() = element.typeParameters.map { context.translateTypeParameterElement(it as TypeParameterElement) }
            override val packageName: String
                get() = element.getPackageName()
            override val parentDeclaration: KElement<*>?
                get() = element.getParentKDeclaration(context)

            override fun asType(): KExecutableType {
                return context.translateExecutableType(element.asType())
            }

            override val annotations: Sequence<KAnnotationMirror>
                get() = element.getKAnnotations(context)

            override val modifiers: Sequence<IModifier>
                get() = element.getKModifiers()
        }
    }
}