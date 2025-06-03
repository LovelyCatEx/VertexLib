package com.lovelycatv.vertex.lang.adapter.java

import com.lovelycatv.vertex.lang.adapter.AbstractElementAdapter
import com.lovelycatv.vertex.lang.model.element.KElement
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror

/**
 * @author lovelycat
 * @since 2025-06-01 18:43
 * @version 1.0
 */
abstract class AbstractJavaElementAdapter<TARGET: Element, R: KElement<*>>(
    context: JavaAdapterContext
) : AbstractElementAdapter<TARGET, R, AnnotationMirror, Element, TypeMirror>(context)