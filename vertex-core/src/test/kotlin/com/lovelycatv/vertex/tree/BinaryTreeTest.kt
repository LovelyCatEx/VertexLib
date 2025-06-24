package com.lovelycatv.vertex.tree

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class BinaryTreeTest {
    companion object {
        private lateinit var fullBinaryTree: BinaryTree<Int>
        private lateinit var completeBinaryTree: BinaryTree<Int>
    }

    @BeforeEach
    fun beforeEach() {
        fullBinaryTree = BinaryTree(
            value = 1,
            left = BinaryTree(
                value = 2,
                left = BinaryTree(4),
                right = BinaryTree(5)
            ),
            right = BinaryTree(
                value = 3,
                left = BinaryTree(6),
                right = BinaryTree(7)
            )
        )

        completeBinaryTree = BinaryTree(
            value = 1,
            left = BinaryTree(
                value = 2,
                left = BinaryTree(4),
                right = BinaryTree(5)
            ),
            right = BinaryTree(
                value = 3,
                left = BinaryTree(6),
                right = null
            )
        )
    }

    @Test
    fun getLeft() {
        assertTrue(completeBinaryTree.left!!.value == 2)
    }

    @Test
    fun getRight() {
        assertTrue(completeBinaryTree.right!!.value == 3)
    }

    @Test
    fun getSize() {
        assertTrue(completeBinaryTree.size == 2)
    }

    @Test
    fun getValidSize() {
        assertTrue(completeBinaryTree[1].validSize == 1)
    }

    @Test
    fun inOrderAccess() {
        val expected = listOf(4, 2, 5, 1, 6, 3)
        val result = mutableListOf<Int>()
        completeBinaryTree.inOrderAccess {
            result.add(it.value)
        }
        assertTrue(expected.joinToString() == result.joinToString())
    }

    @Test
    fun isFullBinaryTree() {
        assertTrue(fullBinaryTree.isFullBinaryTree())
    }

    @Test
    fun isCompleteBinaryTree() {
        assertTrue(fullBinaryTree.isCompleteBinaryTree())
        assertTrue(completeBinaryTree.isCompleteBinaryTree())
        assertFalse(completeBinaryTree.apply { this[0].remove(0) }.isCompleteBinaryTree())
    }
}