package com.lovelycatv.vertex.lang.model.annotation

import com.lovelycatv.vertex.lang.model.element.KVariableElement
import com.lovelycatv.vertex.lang.model.type.KDeclaredType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror

/**
 * @author lovelycat
 * @since 2025-05-29 23:20
 * @version 1.0
 */
interface KAnnotationMirror {
    val annotationType: KDeclaredType

    val fields: Map<KVariableElement<KTypeMirror>, KAnnotationValue>
}