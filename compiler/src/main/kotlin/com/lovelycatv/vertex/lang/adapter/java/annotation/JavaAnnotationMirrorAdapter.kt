package com.lovelycatv.vertex.lang.adapter.java.annotation

import com.lovelycatv.vertex.lang.adapter.java.AbstractJavaAnnotationAdapter
import com.lovelycatv.vertex.lang.adapter.java.JavaAdapterContext
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationValue
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue

/**
 * @author lovelycat
 * @since 2025-06-03 21:41
 * @version 1.0
 */
class JavaAnnotationMirrorAdapter(
    context: JavaAdapterContext
) : AbstractJavaAnnotationAdapter<AnnotationMirror, KAnnotationMirror>(context) {
    override fun translate(annotation: AnnotationMirror): KAnnotationMirror {
        return object : KAnnotationMirror {
            override val annotationType: KDeclaredType
                get() = context.translateDeclaredType(annotation.annotationType)
            override val fields: Map<KTypeMirror, KAnnotationValue>
                get() = annotation.elementValues.mapKeys { (executableElement, _) ->
                    context.translateType(executableElement.returnType)
                }.mapValues { (_, annotationValue: AnnotationValue) ->
                    object : KAnnotationValue {
                        override val value: Any?
                            get() = annotationValue.value
                    }
                }

        }
    }
}