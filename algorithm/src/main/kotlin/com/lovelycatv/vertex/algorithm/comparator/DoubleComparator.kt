package com.lovelycatv.vertex.algorithm.comparator

import com.lovelycatv.vertex.algorithm.Comparator

/**
 * @author lovelycat
 * @since 2025-09-22 16:18
 * @version 1.0
 */
class DoubleComparator : Comparator<Double> {
    override fun compare(v1: Double, v2: Double): Int {
        val r = v1 - v2
        return if (r > 0) 1 else if (r < 0) -1 else 0
    }
}