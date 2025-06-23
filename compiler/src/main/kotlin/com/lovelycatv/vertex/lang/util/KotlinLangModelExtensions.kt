package com.lovelycatv.vertex.lang.util

import com.google.devtools.ksp.symbol.*
import com.lovelycatv.vertex.lang.adapter.AbstractAdapterContext
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.getJVMThrowsAnnotation
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.modifier.JavaModifier
import com.lovelycatv.vertex.lang.modifier.KotlinModifier
import com.lovelycatv.vertex.lang.modifier.SharedModifier
import kotlin.jvm.Throws

/**
 * @author lovelycat
 * @since 2025-06-23 16:00
 * @version 1.0
 */
class KotlinLangModelExtensions private constructor()

fun Sequence<KSAnnotation>.toKAnnotations(context: AbstractKotlinAdapterContext): Sequence<KAnnotationMirror> {
    return this.map { context.translateAnnotation(it) }
}

@Suppress("UNCHECKED_CAST")
fun Sequence<KSAnnotation>.getThrowTypes(context: AbstractKotlinAdapterContext): Sequence<KDeclaredType> {
    return this.getJVMThrowsAnnotation().flatMap {
        it.arguments.find { it.name?.asString() == "exceptionClasses" }?.value as? List<KSType> ?: emptyList()
    }.mapNotNull { if (it.declaration is KSClassDeclaration) context.translateDeclaredType(it) else null }
}

fun KSAnnotated.getThrowTypes(context: AbstractKotlinAdapterContext): Sequence<KDeclaredType> {
    return this.annotations.getThrowTypes(context)
}

fun KSAnnotated.getKAnnotations(context: AbstractKotlinAdapterContext): Sequence<KAnnotationMirror> {
    return this.annotations.toKAnnotations(context)
}

fun KSModifierListOwner.getKModifiers(): Sequence<IModifier> {
    return this.modifiers.map {
        SharedModifier.getModifierByName(it.name) ?: KotlinModifier.getModifierByName(it.name)
        ?: throw IllegalStateException("Unrecognized kotlin modifier: ${it.name}")
    }.asSequence()
}

fun KSDeclaration.getParentKDeclaration(context: AbstractKotlinAdapterContext): KElement<*>? {
    return this.parentDeclaration?.let { context.translateElement(it) }
}