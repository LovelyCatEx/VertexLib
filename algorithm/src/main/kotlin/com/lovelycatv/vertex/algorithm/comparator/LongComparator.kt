package com.lovelycatv.vertex.algorithm.comparator

import com.lovelycatv.vertex.algorithm.Comparator

/**
 * @author lovelycat
 * @since 2025-09-22 16:18
 * @version 1.0
 */
class LongComparator : Comparator<Long> {
    override fun compare(v1: Long, v2: Long): Int {
        val r = v1 - v2
        return if (r > 0) 1 else if (r < 0) -1 else 0
    }
}