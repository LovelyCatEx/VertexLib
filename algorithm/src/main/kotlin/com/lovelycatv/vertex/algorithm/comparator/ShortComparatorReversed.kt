package com.lovelycatv.vertex.algorithm.comparator

import com.lovelycatv.vertex.algorithm.Comparator

/**
 * @author lovelycat
 * @since 2025-09-22 16:18
 * @version 1.0
 */
class ShortComparatorReversed : Comparator<Short> {
    override fun compare(v1: Short, v2: Short): Int {
        return v2 - v1
    }
}