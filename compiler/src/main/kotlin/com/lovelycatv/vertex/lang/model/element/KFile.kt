package com.lovelycatv.vertex.lang.model.element

import com.lovelycatv.vertex.lang.model.annotation.KAnnotated

/**
 * @author lovelycat
 * @since 2025-05-29 23:09
 * @version 1.0
 */
interface KFile : KElementContainer, KAnnotated {
    val packageName: String

    val fileName: String

    val filePath: String

    override fun inspect(): List<String> {
        return super.inspect() + listOf(
            "$packageName.$fileName (${filePath})",
            "  > Declarations:",
        ) + this.declarations.flatMap { it.inspect() + listOf("") }.map { " ".repeat(6) + it }
    }
}