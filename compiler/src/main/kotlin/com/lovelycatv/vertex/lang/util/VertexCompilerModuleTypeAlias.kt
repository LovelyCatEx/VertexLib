package com.lovelycatv.vertex.lang.util

import com.google.devtools.ksp.symbol.*
import com.lovelycatv.vertex.lang.adapter.IAdapterContext
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror

/**
 * @author lovelycat
 * @since 2025-06-23 16:18
 * @version 1.0
 */
class VertexCompilerModuleTypeAlias private constructor()

typealias IJavaAdapterContext = IAdapterContext<AnnotationMirror, Element, TypeMirror>

typealias IKotlinAdapterContext = IAdapterContext<KSAnnotation, KSAnnotated, KSType>
