package com.lovelycatv.vertex.lang.adapter.kotlin

import com.google.devtools.ksp.symbol.*
import com.lovelycatv.vertex.lang.adapter.kotlin.annotation.KotlinAnnotationAdapter
import com.lovelycatv.vertex.lang.adapter.kotlin.element.*
import com.lovelycatv.vertex.lang.adapter.kotlin.type.*
import com.lovelycatv.vertex.lang.model.*
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.*
import com.lovelycatv.vertex.lang.model.type.*
import com.lovelycatv.vertex.lang.util.AbstractKotlinAdapterContext

/**
 * @author lovelycat
 * @since 2025-06-01 18:41
 * @version 1.0
 */
class DefaultKotlinAdapterContext : AbstractKotlinAdapterContext() {
    override fun translateAnnotation(annotation: KSAnnotation): KAnnotationMirror {
        return KotlinAnnotationAdapter(this).translate(annotation)
    }

    override fun translateElement(element: KSAnnotated): KElement<*> {
        return when (element) {
            is KSClassDeclaration -> this.translateTypeElement(element)
            is KSTypeParameter -> this.translateTypeParameterElement(element)
            is KSFunctionDeclaration -> this.translateExecutableElement(element)
            is KSPropertyDeclaration -> this.translateVariableElement(element)
            is KSValueParameter -> this.translateVariableElement(element)

            else -> throw IllegalArgumentException("Unsupported element type: ${element::class.qualifiedName}")
        }
    }

    override fun translateTypeElement(element: KSAnnotated): KDeclaredTypeElement {
        return when (element) {
            is KSClassDeclaration -> {
                KotlinTypeElementAdapter(this).translate(element)
            }

            else -> throw IllegalArgumentException("Unsupported element type: ${element::class.qualifiedName}")
        }
    }

    override fun translateTypeParameterElement(element: KSAnnotated): KTypeParameterElement {
        return when (element) {
            is KSTypeParameter -> {
                KotlinTypeParameterAdapter(this).translate(element)
            }

            else -> throw IllegalArgumentException("Unsupported element type: ${element::class.qualifiedName}")
        }
    }

    override fun translateExecutableElement(element: KSAnnotated): KExecutableElement {
        return when (element) {
            is KSFunctionDeclaration -> {
                KotlinExecutableElementAdapter(this).translate(element)
            }

            else -> throw IllegalArgumentException("Unsupported element type: ${element::class.qualifiedName}")
        }
    }

    /**
     * Translate [KSPropertyDeclaration] or [KSValueParameter] to [KVariableElement].
     *
     * In kotlin, variableElement has been subdivided into [KSPropertyDeclaration] and [KSValueParameter], which is not in java.
     *
     * @param element [KSPropertyDeclaration] or [KSValueParameter]
     * @return [KVariableElement]
     */
    override fun translateVariableElement(element: KSAnnotated): KVariableElement<KTypeMirror> {
        return when (element) {
            is KSPropertyDeclaration -> {
                KotlinVariableElementAdapterForProperty(this).translate(element)
            }

            is KSValueParameter -> {
                KotlinVariableElementAdapterForValueParameter(this).translate(element)
            }

            else -> throw IllegalArgumentException("Unsupported element type: ${element::class.qualifiedName}")
        }
    }

    override fun translateType(type: KSType): KTypeMirror {
        return if (type.isArrayType()) {
            // As the array type is also a class type, so this condition should be checked ahead of class
            this.translateArrayType(type)
        } else if (type.isClassType()) {
            this.translateDeclaredType(type)
        } else if (type.isTypeParameter()) {
            this.translateTypeVariable(type)
        } else if (type.isFunctionType()) {
            this.translateExecutableType(type)
        } else if (type.isPrimitiveType()) {
            this.translatePrimitiveType(type)
        } else throw IllegalArgumentException("Unsupported type: $type")
    }

    override fun translateDeclaredType(type: KSType): KDeclaredType {
        return KotlinDeclaredTypeAdapter(this).translate(type)
    }

    override fun translateExecutableType(type: KSType): KExecutableType {
        return KotlinExecutableTypeAdapter(this).translate(type)
    }

    override fun translatePrimitiveType(type: KSType): KPrimitiveType {
        return KotlinPrimitiveTypeAdapter(this).translate(type)
    }

    override fun translateTypeVariable(type: KSType): KTypeVariable {
        return KotlinTypeVariableAdapter(this).translate(type)
    }

    override fun translateWildcardType(type: KSType): KWildcardType {
        throw UnsupportedOperationException("There are no actual WildcardType representation in ksp, possibly be found in typeArguments of KDeclaredType")
    }

    override fun translateArrayType(type: KSType): KArrayType {
        return KotlinArrayTypeAdapter(this).translate(type)
    }


}