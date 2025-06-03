package com.lovelycatv.vertex.lang.adapter.kotlin

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.lovelycatv.vertex.lang.adapter.AbstractAdapterContext
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.*
import com.lovelycatv.vertex.lang.model.type.*

/**
 * @author lovelycat
 * @since 2025-06-01 18:41
 * @version 1.0
 */
class KotlinAdapterContext : AbstractAdapterContext<KSAnnotation, KSDeclaration, KSType>() {
    override fun translateAnnotation(annotation: KSAnnotation): KAnnotationMirror {
        TODO("Not yet implemented")
    }

    override fun translateElement(element: KSDeclaration): KElement<*> {
        TODO("Not yet implemented")
    }

    override fun translateTypeElement(element: KSDeclaration): KDeclaredTypeElement {
        TODO("Not yet implemented")
    }

    override fun translateTypeParameterElement(element: KSDeclaration): KTypeParameterElement {
        TODO("Not yet implemented")
    }

    override fun translateExecutableElement(element: KSDeclaration): KExecutableElement {
        TODO("Not yet implemented")
    }

    override fun translateVariableElement(element: KSDeclaration): KVariableElement<KTypeMirror> {
        TODO("Not yet implemented")
    }

    override fun translateType(type: KSType): KTypeMirror {
        TODO("Not yet implemented")
    }

    override fun translateDeclaredType(type: KSType): KDeclaredType {
        TODO("Not yet implemented")
    }

    override fun translateExecutableType(type: KSType): KExecutableType {
        TODO("Not yet implemented")
    }

    override fun translatePrimitiveType(type: KSType): KPrimitiveType {
        TODO("Not yet implemented")
    }

    override fun translateTypeVariable(type: KSType): KTypeVariable {
        TODO("Not yet implemented")
    }

    override fun translateWildcardType(type: KSType): KWildcardType {
        TODO("Not yet implemented")
    }

    override fun translateArrayType(type: KSType): KArrayType {
        TODO("Not yet implemented")
    }
}