package com.lovelycatv.vertex.algorithm.comparator

import com.lovelycatv.vertex.algorithm.Comparator

/**
 * @author lovelycat
 * @since 2025-09-22 16:18
 * @version 1.0
 */
class IntComparatorReversed : Comparator<Int> {
    override fun compare(v1: Int, v2: Int): Int {
        return v2 - v1
    }
}