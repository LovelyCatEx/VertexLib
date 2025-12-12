package com.lovelycatv.vertex.algorithm.impl

import com.lovelycatv.vertex.algorithm.comparator.LongComparator
import com.lovelycatv.vertex.algorithm.comparator.LongComparatorReversed
import com.lovelycatv.vertex.algorithm.sort.impl.SelectSort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SelectSortTest {

    @Test
    fun sort() {
        val arr = arrayOf(1L, 4L, 2L, 9L, 6L, 8L, 7L, 5L)
        val list = mutableListOf(1L, 4L, 2L, 9L, 6L, 8L, 7L, 5L)

        println("Original:")
        println(arr.joinToString())
        println(list.joinToString())

        val algorithm = SelectSort<Long>()
        algorithm.sort(arr, LongComparator())
        algorithm.sort(list, LongComparatorReversed())

        println("Sorted:")
        println(arr.joinToString())
        println(list.joinToString())

        val answer1 = arrayOf(1L, 2L, 4L, 5L, 6L, 7L, 8L, 9L)
        val answer2 = arrayOf(9L, 8L, 7L, 6L, 5L, 4L, 2L, 1L)

        for (i in answer1.indices) {
            assertEquals(arr[i], answer1[i])
        }

        for (i in answer2.indices) {
            assertEquals(list[i], answer2[i])
        }
    }
}