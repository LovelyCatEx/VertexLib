package com.lovelycatv.vertex.algorithm.sort

import com.lovelycatv.vertex.algorithm.Comparator
import com.lovelycatv.vertex.collection.fitList

/**
 * @author lovelycat
 * @since 2025-09-22 15:42
 * @version 1.0
 */
interface SortAlgorithm<T> : LegacySortAlgorithm<T> {
    fun sort(list: MutableList<T>, comparator: Comparator<T>) {
        this.sort(list, comparator, 0, list.size - 1)
    }

    fun sort(arr: Array<T>, comparator: Comparator<T>) {
        this.sort(arr.fitList(), comparator)
    }
}