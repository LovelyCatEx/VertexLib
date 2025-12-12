package com.lovelycatv.vertex.algorithm.sort.impl

import com.lovelycatv.vertex.algorithm.Comparator
import com.lovelycatv.vertex.algorithm.sort.LegacySortAlgorithm
import com.lovelycatv.vertex.algorithm.sort.SortAlgorithm

/**
 * @author lovelycat
 * @since 2025-09-22 16:29
 * @version 1.0
 */
class SelectSort<T> : SortAlgorithm<T> {
    override fun sort(list: MutableList<T>, comparator: Comparator<T>, l: Int, r: Int) {
        if (LegacySortAlgorithm.check(list, l, r)) {
            return
        }

        var currentMinIndex: Int
        for (i in l..r) {
            currentMinIndex = i

            for (j in i + 1..r) {
                if (comparator.compare(list[j], list[currentMinIndex]) < 0) {
                    currentMinIndex = j
                }
            }

            if (i != currentMinIndex) {
                val t = list[i]
                list[i] = list[currentMinIndex]
                list[currentMinIndex] = t
            }
        }
    }
}