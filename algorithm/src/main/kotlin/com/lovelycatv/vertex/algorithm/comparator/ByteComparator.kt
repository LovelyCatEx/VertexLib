package com.lovelycatv.vertex.algorithm.comparator

import com.lovelycatv.vertex.algorithm.Comparator

/**
 * @author lovelycat
 * @since 2025-09-22 16:18
 * @version 1.0
 */
class ByteComparator : Comparator<Byte> {
    override fun compare(v1: Byte, v2: Byte): Int {
        val r = v1 - v2
        return if (r > 0) 1 else if (r < 0) -1 else 0
    }
}