package com.lovelycatv.vertex.lang.adapter.kotlin.annotation

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.lovelycatv.vertex.lang.adapter.ActualKName
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinAnnotationAdapter
import com.lovelycatv.vertex.lang.adapter.kotlin.DefaultKotlinAdapterContext
import com.lovelycatv.vertex.lang.model.KName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotated
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationValue
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.element.KVariableElement
import com.lovelycatv.vertex.lang.model.getPackageName
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.modifier.IModifier
import com.lovelycatv.vertex.lang.util.getKAnnotations
import com.lovelycatv.vertex.lang.util.getKModifiers

/**
 * @author lovelycat
 * @since 2025-06-09 13:01
 * @version 1.0
 */
class KotlinAnnotationAdapter(
    context: DefaultKotlinAdapterContext
) : AbstractKotlinAnnotationAdapter<KSAnnotation, KAnnotationMirror>(context) {
    override fun translate(annotation: KSAnnotation): KAnnotationMirror {
        return object : KAnnotationMirror {
            override val annotationType: KDeclaredType
                get() = context.translateDeclaredType(annotation.annotationType.resolve())
            override val fields: Map<KVariableElement<KTypeMirror>, KAnnotationValue>
                get() = (annotation.annotationType.resolve().declaration as KSClassDeclaration).let { annotationDeclaration ->
                    annotationDeclaration.getDeclaredProperties().map { property ->
                        val correspondingKSValueArgument = annotation.arguments.find {
                            it.name?.getShortName() == property.simpleName.getShortName()
                        }

                        val correspondingDefaultKSValueArgument = annotation.defaultArguments.find {
                            it.name?.getShortName() == property.simpleName.getShortName()
                        }

                        object : KVariableElement<KTypeMirror> {
                            override val original: Any
                                get() = property
                            override val language: KAnnotated.Language
                                get() = KAnnotated.Language.KOTLIN

                            override fun asType(): KTypeMirror {
                                return context.translateType(property.type.resolve())
                            }

                            override val constantValue: Any?
                                get() = correspondingDefaultKSValueArgument?.value
                            override val name: KName
                                get() = ActualKName(property.simpleName.getShortName(), null)
                            override val packageName: String
                                get() = annotation.annotationType.getPackageName()
                            override val parentDeclaration: KElement<*>
                                get() = context.translateElement(annotationDeclaration)
                            override val annotations: Sequence<KAnnotationMirror>
                                get() = property.getKAnnotations(context)
                            override val modifiers: Sequence<IModifier>
                                get() = property.getKModifiers()

                        } to object : KAnnotationValue {
                            override val value: Any?
                                get() = correspondingKSValueArgument?.value
                        }
                    }.toMap()
                }

        }
    }
}