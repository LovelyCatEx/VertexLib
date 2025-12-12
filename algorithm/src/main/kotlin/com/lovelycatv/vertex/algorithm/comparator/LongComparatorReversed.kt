package com.lovelycatv.vertex.algorithm.comparator

import com.lovelycatv.vertex.algorithm.Comparator

/**
 * @author lovelycat
 * @since 2025-09-22 16:18
 * @version 1.0
 */
class LongComparatorReversed : Comparator<Long> {
    override fun compare(v1: Long, v2: Long): Int {
        val r = v2 - v1
        return if (r > 0) 1 else if (r < 0) -1 else 0
    }
}