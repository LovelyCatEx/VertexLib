package com.lovelycatv.vertex.lang.util

import com.lovelycatv.vertex.lang.model.annotation.KAnnotated
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.type.KTypeMirror

/**
 * @author lovelycat
 * @since 2025-06-26 02:59
 * @version 1.0
 */
class VertexCompilerExtensions private constructor()

fun Collection<KAnnotated>.filterAllElements() = this.filterIsInstance<KElement<*>>()

inline fun <reified E: KElement<*>> Collection<KAnnotated>.filterElements() = this.filterIsInstance<E>()

fun Collection<KAnnotated>.filterAllTypes() = this.filterIsInstance<KTypeMirror>()

inline fun <reified T: KTypeMirror> Collection<KAnnotated>.filterTypes() = this.filterIsInstance<T>()