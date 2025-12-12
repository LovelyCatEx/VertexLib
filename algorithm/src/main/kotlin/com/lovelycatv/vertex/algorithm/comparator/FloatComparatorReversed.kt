package com.lovelycatv.vertex.algorithm.comparator

import com.lovelycatv.vertex.algorithm.Comparator

/**
 * @author lovelycat
 * @since 2025-09-22 16:18
 * @version 1.0
 */
class FloatComparatorReversed : Comparator<Float> {
    override fun compare(v1: Float, v2: Float): Int {
        val r = v2 - v1
        return if (r > 0) 1 else if (r < 0) -1 else 0
    }
}