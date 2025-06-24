package com.lovelycatv.vertex.lang.model.type

/**
 * @author lovelycat
 * @since 2025-06-04 20:17
 * @version 1.0
 */
interface KNoType : KTypeMirror {
    override fun inspect(): List<String> {
        return super.inspect() + listOf()
    }
}