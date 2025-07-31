package com.lovelycatv.vertex.json

/**
 * @author lovelycat
 * @since 2025-08-01 00:26
 * @version 1.0
 */
interface JSONNode {
    val elementSize: Int

    fun toJSONString(): String
}