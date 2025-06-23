package com.lovelycatv.vertex.lang.adapter.java.annotation

import com.lovelycatv.vertex.lang.adapter.ActualKName
import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaAnnotationAdapter
import com.lovelycatv.vertex.lang.adapter.java.DefaultJavaAdapterContext
import com.lovelycatv.vertex.lang.model.KName
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
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue

/**
 * @author lovelycat
 * @since 2025-06-03 21:41
 * @version 1.0
 */
class JavaAnnotationMirrorAdapter(
    context: DefaultJavaAdapterContext
) : AbstractJavaAnnotationAdapter<AnnotationMirror, KAnnotationMirror>(context) {
    override fun translate(annotation: AnnotationMirror): KAnnotationMirror {
        return object : KAnnotationMirror {
            override val annotationType: KDeclaredType
                get() = context.translateDeclaredType(annotation.annotationType)
            override val fields: Map<KVariableElement<KTypeMirror>, KAnnotationValue>
                get() = annotation.elementValues.mapKeys { (executableElement, _) ->
                    object : KVariableElement<KTypeMirror> {
                        override fun asType(): KTypeMirror {
                            return context.translateType(executableElement.returnType)
                        }

                        override val constantValue: Any?
                            get() = executableElement.defaultValue?.value
                        override val name: KName
                            get() = ActualKName(executableElement.simpleName.toString(), null)
                        override val packageName: String
                            get() = executableElement.getPackageName()
                        override val parentDeclaration: KElement<*>
                            get() = context.translateExecutableElement(executableElement)
                        override val annotations: Sequence<KAnnotationMirror>
                            get() = executableElement.getKAnnotations(context)
                        override val modifiers: Sequence<IModifier>
                            get() = executableElement.getKModifiers()

                    }
                }.mapValues { (_, annotationValue: AnnotationValue) ->
                    object : KAnnotationValue {
                        override val value: Any?
                            get() = annotationValue?.value
                    }
                }

        }
    }
}