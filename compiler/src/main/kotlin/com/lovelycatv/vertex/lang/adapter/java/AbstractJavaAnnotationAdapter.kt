package com.lovelycatv.vertex.lang.adapter.java

import com.lovelycatv.vertex.lang.adapter.AbstractAnnotationAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror

/**
 * @author lovelycat
 * @since 2025-06-01 18:43
 * @version 1.0
 */
abstract class AbstractJavaAnnotationAdapter<TARGET: AnnotationMirror, R: KAnnotationMirror>(
    context: JavaAdapterContext
) : AbstractAnnotationAdapter<TARGET, R, AnnotationMirror, Element, TypeMirror>(context)