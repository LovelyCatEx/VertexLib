package com.lovelycatv.vertex.lang.model

/**
 * @author lovelycat
 * @since 2025-05-29 23:24
 * @version 1.0
 */
interface KName {
    val simpleName: String

    val qualifiedName: String?

    override fun toString(): String
}