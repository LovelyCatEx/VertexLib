package com.lovelycatv.vertex.lang.adapter.java

import com.lovelycatv.vertex.lang.adapter.java.annotation.JavaAnnotationMirrorAdapter
import com.lovelycatv.vertex.lang.adapter.java.element.JavaExecutableElementAdapter
import com.lovelycatv.vertex.lang.adapter.java.element.JavaTypeElementAdapter
import com.lovelycatv.vertex.lang.adapter.java.element.JavaTypeParameterElementAdapter
import com.lovelycatv.vertex.lang.adapter.java.element.JavaVariableElementAdapter
import com.lovelycatv.vertex.lang.adapter.java.type.*
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.*
import com.lovelycatv.vertex.lang.model.type.*
import com.lovelycatv.vertex.lang.util.AbstractJavaAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations
import javax.lang.model.element.*
import javax.lang.model.type.*

/**
 * @author lovelycat
 * @since 2025-06-01 18:41
 * @version 1.0
 */
class DefaultJavaAdapterContext : AbstractJavaAdapterContext() {
    override fun translateAnnotation(annotation: AnnotationMirror): KAnnotationMirror {
        return JavaAnnotationMirrorAdapter(this).translate(annotation)
    }

    override fun translateElement(element: Element): KElement<*> {
        return when (element) {
            is TypeElement -> this.translateTypeElement(element)
            is TypeParameterElement -> this.translateTypeElement(element)
            is ExecutableElement -> this.translateExecutableElement(element)
            is VariableElement -> this.translateVariableElement(element)
            else -> throw IllegalStateException("Unsupported element type: ${element::class.qualifiedName}")
        }
    }

    override fun translateTypeElement(element: Element): KDeclaredTypeElement {
        return JavaTypeElementAdapter(this).translate(element as TypeElement)
    }

    override fun translateTypeParameterElement(element: Element): KTypeParameterElement {
        return JavaTypeParameterElementAdapter(this).translate(element as TypeParameterElement)
    }

    override fun translateExecutableElement(element: Element): KExecutableElement {
        return JavaExecutableElementAdapter(this).translate(element as ExecutableElement)
    }

    override fun translateVariableElement(element: Element): KVariableElement<KTypeMirror> {
        return JavaVariableElementAdapter(this).translate(element as VariableElement)
    }

    override fun translateType(type: TypeMirror): KTypeMirror {
        return when (type) {
            is DeclaredType -> this.translateDeclaredType(type)
            is ExecutableType -> this.translateExecutableType(type)
            is PrimitiveType -> this.translatePrimitiveType(type)
            is TypeVariable -> this.translateTypeVariable(type)
            is WildcardType -> this.translateWildcardType(type)
            is ArrayType -> this.translateArrayType(type)
            is NoType -> object : KNoType {
                override fun toString(): String {
                    return type.kind.toString().lowercase()
                }

                override val annotations: Sequence<KAnnotationMirror>
                    get() = type.getKAnnotations(this@DefaultJavaAdapterContext)

                override fun inspect(): List<String> {
                    return listOf(this.toString())
                }
            }
            else -> throw IllegalStateException("Unsupported type: ${type::class.qualifiedName}")
        }
    }

    override fun translateDeclaredType(type: TypeMirror): KDeclaredType {
        return JavaDeclaredTypeAdapter(this).translate(type as DeclaredType)
    }

    override fun translateExecutableType(type: TypeMirror): KExecutableType {
        return JavaExecutableTypeAdapter(this).translate(type as ExecutableType)
    }

    override fun translatePrimitiveType(type: TypeMirror): KPrimitiveType {
        return JavaPrimitiveTypeAdapter(this).translate(type as PrimitiveType)
    }

    override fun translateTypeVariable(type: TypeMirror): KTypeVariable {
        return JavaTypeVariableAdapter(this).translate(type as TypeVariable)
    }

    override fun translateWildcardType(type: TypeMirror): KWildcardType {
        return JavaWildcardTypeAdapter(this).translate(type as WildcardType)
    }

    override fun translateArrayType(type: TypeMirror): KArrayType {
        return JavaArrayTypeAdapter(this).translate(type as ArrayType)
    }
}