package com.lovelycatv.vertex.algorithm.sort.impl

import com.lovelycatv.vertex.algorithm.Comparator
import com.lovelycatv.vertex.algorithm.sort.LegacySortAlgorithm
import com.lovelycatv.vertex.algorithm.sort.SortAlgorithm

/**
 * @author lovelycat
 * @since 2025-09-22 15:41
 * @version 1.0
 */
class BubbleSort<T> : SortAlgorithm<T> {
    override fun sort(list: MutableList<T>, comparator: Comparator<T>, l: Int, r: Int) {
        if (LegacySortAlgorithm.check(list, l, r)) {
            return
        }

        var t: T
        for (i in l..r) {
            for (j in i + 1..r) {
                if (comparator.compare(list[i], list[j]) > 0) {
                    t = list[i]
                    list[i] = list[j]
                    list[j] = t
                }
            }
        }
    }
}