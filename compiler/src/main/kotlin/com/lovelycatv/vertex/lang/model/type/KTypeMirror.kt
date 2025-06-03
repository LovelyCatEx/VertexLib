package com.lovelycatv.vertex.lang.model.type

import com.lovelycatv.vertex.lang.model.annotation.KAnnotationContainer

/**
 * @author lovelycat
 * @since 2025-05-29 23:36
 * @version 1.0
 */
interface KTypeMirror : KAnnotationContainer {
    /**
     * Returns the qualified name of the type
     *
     * @return The qualified name of the type
     */
    override fun toString(): String
}