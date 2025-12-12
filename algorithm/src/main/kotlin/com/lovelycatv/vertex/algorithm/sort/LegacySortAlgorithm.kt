package com.lovelycatv.vertex.algorithm.sort

import com.lovelycatv.vertex.algorithm.Comparator
import com.lovelycatv.vertex.collection.fitList

/**
 * @author lovelycat
 * @since 2025-09-22 16:54
 * @version 1.0
 */
interface LegacySortAlgorithm<T> {
    fun sort(list: MutableList<T>, comparator: Comparator<T>, l: Int, r: Int)

    fun sort(arr: Array<T>, comparator: Comparator<T>, l: Int, r: Int) {
        this.sort(arr.fitList(), comparator, l, r)
    }

    companion object {
        fun check(list: MutableList<*>, l: Int, r: Int): Boolean {
            return list.isEmpty() || list.size == 1 || l >= r
        }
    }
}