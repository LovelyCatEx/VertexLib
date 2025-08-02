package com.lovelycatv.vertex.asm

/**
 * @author lovelycat
 * @since 2025-08-01 18:20
 * @version 1.0
 */
enum class JavaKeyWord : IJavaKeyWord {
    CLASS, INTERFACE, ENUM, RECORD, SUPER;

    override fun getWord(): String {
        return this.name
    }
}