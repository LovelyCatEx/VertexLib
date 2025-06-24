package com.lovelycatv.vertex.lang.model.type

/**
 * Represents a primitive type. These include boolean, byte, short, int, long, char, float, and double.
 *
 * @author lovelycat
 * @since 2025-05-30 14:28
 * @version 1.0
 */
interface KPrimitiveType : KTypeMirror {
    override fun inspect(): List<String> {
        return super.inspect() + listOf()
    }
}