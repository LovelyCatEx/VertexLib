package com.lovelycatv.vertex.algorithm

/**
 * @author lovelycat
 * @since 2025-09-22 16:19
 * @version 1.0
 */
interface Comparator<T> {
    fun compare(v1: T, v2: T): Int
}