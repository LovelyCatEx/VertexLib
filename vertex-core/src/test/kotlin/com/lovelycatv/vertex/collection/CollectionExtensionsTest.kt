package com.lovelycatv.vertex.collection

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class CollectionExtensionsTest {
    private var numberList1 = mutableListOf<Int>()
    private var numberList2 = mutableListOf<Int>()
    private var numberList3 = mutableListOf<Int>()
    private var stringList1 = mutableListOf<String>()
    private var stringList2 = mutableListOf<String>()
    private var stringList3 = mutableListOf<String>()
    @BeforeEach
    open fun beforeEach() {
        numberList1 = mutableListOf(1, 2, 3, 4, 5, 6)
        numberList2 = mutableListOf(7, 8, 9, 10, 11, 12)
        numberList3 = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        stringList1 = mutableListOf("1", "2", "3", "4", "5", "6")
        stringList2 = mutableListOf("7", "8", "9", "10", "11", "12")
        stringList3 = mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    }

    @Test
    open fun merge() {
        println("Merge: $numberList1 and $stringList2")

        val expected = numberList3

        val result = numberList1.merge(stringList2) { it.toInt() }

        println("Expected: $expected")
        println("> Result: $result")

        assertEquals(result.size, expected.size, "Result list size is not equal to the expected.")
        assertTrue(result.containsAll(expected), "Elements in result list is not equal to the expectd.")
    }

    @Test
    open fun mergeTo() {
        println("Merge $numberList1 to $stringList2")

        val expected = stringList3

        val result = numberList1.mergeTo(stringList2) { it.toString() }

        println("Expected: $expected")
        println("> Result: $result")

        assertEquals(result.size, expected.size, "Result list size is not equal to the expected.")
        assertTrue(result.containsAll(expected), "Elements in result list is not equal to the expected.")
    }

    @Test
    open fun leftJoin() {
        println("$numberList1 left join $stringList1")

        val expected = mutableMapOf<Int, List<String>>().apply {
            numberList1.forEach { num ->
                this[num] = listOf(num.toString())
            }
        }

        val result = numberList1.leftJoin(stringList1) { a, b ->
            a == b.toInt()
        }

        println("Expected: $expected")
        println("> Result: $result")

        assertEquals(result.keys.size, expected.keys.size, "Result map keys size is not equal to the expected.")
        assertTrue(result.keys.containsAll(expected.keys), "Keys in result map is not equal to the expected.")
    }

    @Test
    fun rightJoin() {
        println("$numberList1 right join $stringList1")

        val expected = mutableMapOf<String, List<Int>>().apply {
            stringList1.forEach { numStr ->
                this[numStr] = listOf(numStr.toInt())
            }
        }

        val result = numberList1.rightJoin(stringList1) { a, b ->
            a == b.toString()
        }

        println("Expected: $expected")
        println("> Result: $result")

        assertEquals(result.keys.size, expected.keys.size, "Result map keys size is not equal to the expected.")
        assertTrue(result.keys.containsAll(expected.keys), "Keys in result map is not equal to the expected.")
    }

    @Test
    fun indexesOf() {
        val target = "Hello, World!"
        println("Indexes of 'l' in $target")

        val expected = listOf(2, 3, 10)

        val result = target.toList().indexesOf('l')

        println("Expected: $expected")
        println("> Result: $result")

        assertEquals(result.size, expected.size, "Result list size is not equal to the expected.")
        assertTrue(result.containsAll(expected), "Elements in result list is not equal to the expected.")
    }

    @Test
    fun removeAt() {
        println("Remove [1, 3, 5] from $stringList1")

        val expected = listOf("2", "4", "6")

        val result = stringList1.apply {
            removeAt(0, 2, 4)
        }

        println("Expected: $expected")
        println("> Result: $result")
        assertEquals(result.size, expected.size, "Result list size is not equal to the expected.")
        assertTrue(result.containsAll(expected), "Elements in result list is not equal to the expected.")
    }

    @Test
    fun divide() {
        println("Find all odd and even numbers in $numberList3")

        val expectedOdd = listOf(1, 3, 5, 7, 9, 11)
        val expectedEven = listOf(2, 4, 6, 8, 10, 12)

        val (odd, even) = numberList3.divide { it % 2 == 0 }

        println("Expected: $expectedOdd")
        println("> Result: $odd")

        println("Expected: $expectedEven")
        println("> Result: $even")

        assertEquals(odd.size, expectedOdd.size, "Odd result list size is not equal to the expected.")
        assertTrue(odd.containsAll(expectedOdd), "Elements in odd result list is not equal to the expected.")

        assertEquals(even.size, expectedEven.size, "Even result list size is not equal to the expected.")
        assertTrue(even.containsAll(expectedEven), "Elements in even result list is not equal to the expected.")
    }
}