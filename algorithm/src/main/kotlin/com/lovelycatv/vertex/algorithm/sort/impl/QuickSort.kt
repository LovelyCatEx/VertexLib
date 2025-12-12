package com.lovelycatv.vertex.algorithm.sort.impl

import com.lovelycatv.vertex.algorithm.Comparator
import com.lovelycatv.vertex.algorithm.sort.LegacySortAlgorithm
import com.lovelycatv.vertex.algorithm.sort.SortAlgorithm

/**
 * @author lovelycat
 * @since 2025-09-22 16:42
 * @version 1.0
 */
class QuickSort<T> : SortAlgorithm<T> {
    override fun sort(list: MutableList<T>, comparator: Comparator<T>, l: Int, r: Int) {
        if (LegacySortAlgorithm.check(list, l, r)) {
            return
        }

        val p = this.partition(list, comparator, l, r)
        this.sort(list, comparator, l, p)
        this.sort(list, comparator, p + 1, r)
    }

    fun partition(list: MutableList<T>, comparator: Comparator<T>): Int {
        return this.partition(list, comparator, 0, list.size - 1)
    }

    fun partition(list: MutableList<T>, comparator: Comparator<T>, l: Int, r: Int): Int {
        var low = l
        var high = r
        val pivot = list[low]

        while (low != high) {
            while (low < high && comparator.compare(list[high], pivot) >= 0) {
                high--
            }
            list[low] = list[high]
            while (low < high && comparator.compare(list[low], pivot) <= 0) {
                low++
            }
            list[high] = list[low]
        }

        list[low] = pivot

        return high
    }
}