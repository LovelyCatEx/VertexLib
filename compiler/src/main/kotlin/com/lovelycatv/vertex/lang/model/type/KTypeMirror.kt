package com.lovelycatv.vertex.lang.model.type

import com.lovelycatv.vertex.lang.model.annotation.KAnnotated

/**
 * @author lovelycat
 * @since 2025-05-29 23:36
 * @version 1.0
 */
interface KTypeMirror : KAnnotated {
    /**
     * Returns the qualified name of the type
     *
     * @return The qualified name of the type
     */
    override fun toString(): String
}