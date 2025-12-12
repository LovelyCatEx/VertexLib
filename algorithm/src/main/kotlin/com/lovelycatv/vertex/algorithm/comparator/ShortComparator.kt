package com.lovelycatv.vertex.algorithm.comparator

import com.lovelycatv.vertex.algorithm.Comparator

/**
 * @author lovelycat
 * @since 2025-09-22 16:18
 * @version 1.0
 */
class ShortComparator : Comparator<Short> {
    override fun compare(v1: Short, v2: Short): Int {
        return v1 - v2
    }
}