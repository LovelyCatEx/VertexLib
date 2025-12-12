package com.lovelycatv.vertex.algorithm.impl

import com.lovelycatv.vertex.algorithm.comparator.IntComparator
import com.lovelycatv.vertex.algorithm.comparator.IntComparatorReversed
import com.lovelycatv.vertex.algorithm.sort.impl.QuickSort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QuickSortTest {

    @Test
    fun sort() {
        val arr = arrayOf(1, 4, 2, 9, 6, 8, 7, 5)
        val list = mutableListOf(1, 4, 2, 9, 6, 8, 7, 5)

        println("Original:")
        println(arr.joinToString())
        println(list.joinToString())

        val algorithm = QuickSort<Int>()
        algorithm.sort(arr, IntComparator())
        algorithm.sort(list, IntComparatorReversed())

        println("Sorted:")
        println(arr.joinToString())
        println(list.joinToString())

        val answer1 = arrayOf(1, 2, 4, 5, 6, 7, 8, 9)
        val answer2 = arrayOf(9, 8, 7, 6, 5, 4, 2, 1)

        for (i in answer1.indices) {
            assertEquals(arr[i], answer1[i])
        }

        for (i in answer2.indices) {
            assertEquals(list[i], answer2[i])
        }
    }

    @Test
    fun partition() {
        val arr = mutableListOf(4, 3, 6, 1, 7, 2, 8, 5)
        val answer = arrayOf(2, 3, 1, 4, 7, 6, 8, 5)
        val p = QuickSort<Int>().partition(arr, IntComparator())

        assertEquals(3, p)
        for (i in answer.indices) {
            assertEquals(answer[i], arr[i])
        }
    }
}