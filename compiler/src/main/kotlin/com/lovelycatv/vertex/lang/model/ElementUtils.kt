package com.lovelycatv.vertex.lang.model

import javax.lang.model.element.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * @author lovelycat
 * @since 2025-05-27 18:47
 * @version 1.0
 */
class ElementUtils private constructor()

fun Element.getAnnotationMirrorOrNull(annotationClazz: Class<out Annotation>): AnnotationMirror? {
    return this.annotationMirrors.find { it.compareType(annotationClazz) }
}

@OptIn(ExperimentalContracts::class)
fun Element.isClassElement(): Boolean {
    contract {
        returns(true) implies (this@isClassElement is TypeElement)
    }

    return this is TypeElement
}

@OptIn(ExperimentalContracts::class)
fun Element.isExecutableElement(): Boolean {
    contract {
        returns(true) implies (this@isExecutableElement is ExecutableElement)
    }

    return this is ExecutableElement
}

@OptIn(ExperimentalContracts::class)
fun Element.isVariableElement(): Boolean {
    contract {
        returns(true) implies (this@isVariableElement is VariableElement)
    }

    return this is VariableElement
}

@OptIn(ExperimentalContracts::class)
fun Element.isTypeParameterElement(): Boolean {
    contract {
        returns(true) implies (this@isTypeParameterElement is TypeParameterElement)
    }

    return this is TypeParameterElement
}

fun Element.getPackageName(): String {
    var current: Element = this
    while (current.kind != ElementKind.PACKAGE) {
        current = current.enclosingElement
    }
    return (current as PackageElement).qualifiedName.toString()
}