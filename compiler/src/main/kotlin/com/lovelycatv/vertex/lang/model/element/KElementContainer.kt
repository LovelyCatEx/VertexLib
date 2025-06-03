package com.lovelycatv.vertex.lang.model.element

/**
 * @author lovelycat
 * @since 2025-05-29 23:10
 * @version 1.0
 */
interface KElementContainer {
    val declarations: Sequence<KElement<*>>
}