package com.lovelycatv.vertex.lang.util

import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.modifier.JavaModifier
import com.lovelycatv.vertex.lang.modifier.SharedModifier
import javax.lang.model.AnnotatedConstruct
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.type.DeclaredType

/**
 * @author lovelycat
 * @since 2025-06-01 19:35
 * @version 1.0
 */
class JavaElementExtensions private constructor()

fun AnnotatedConstruct.getKAnnotations(context: IJavaAdapterContext): Sequence<KAnnotationMirror> {
    return this.annotationMirrors.map { context.translateAnnotation(it) }.asSequence()
}

fun Element.getKModifiers(): Sequence<IModifier> {
    return this.modifiers.map {
        SharedModifier.getModifierByName(it.name) ?: JavaModifier.getModifierByName(it.name)
        ?: throw IllegalStateException("Unrecognized java modifier: ${it.name}")
    }.asSequence()
}

fun Element.getParentKDeclaration(context: IJavaAdapterContext): KElement<*>? {
    return this.enclosingElement?.run {
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

            else -> null
        }
    }
}

fun DeclaredType.getParentKType(context: IJavaAdapterContext): KDeclaredType? {
    return this.enclosingType?.run {
        when (this) {
            is DeclaredType -> {
                context.translateDeclaredType(this)
            }

            else -> null
        }
    }
}

fun Element.getChildrenDeclarations(context: IJavaAdapterContext): Sequence<KElement<*>> {
    return this.enclosedElements.map { context.translateElement(it) }.asSequence()
}
