package com.lovelycatv.vertex.lang.adapter.java

import com.lovelycatv.vertex.lang.adapter.AbstractTypeAdapter
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.util.AbstractJavaAdapterContext
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror

/**
 * @author lovelycat
 * @since 2025-06-01 18:43
 * @version 1.0
 */
abstract class AbstractJavaTypeAdapter<TARGET: TypeMirror, R: KTypeMirror>(
    context: AbstractJavaAdapterContext
) : AbstractTypeAdapter<TARGET, R, AnnotationMirror, Element, TypeMirror>(context)