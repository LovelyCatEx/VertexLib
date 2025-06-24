package com.lovelycatv.vertex.tree

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class TreeUtilsTest {
    companion object {
        private lateinit var tree1: Tree<Int>
        private lateinit var binaryTree: BinaryTree<Int>
    }

    @BeforeEach
    fun beforeEach() {
        tree1 = Tree(
            value = 1,
            children = arrayOf(
                Tree(
                    value = 2,
                    children = arrayOf(
                        Tree(value = 4),
                        Tree(value = 5)
                    )
                ),
                Tree(
                    value = 3,
                    children = arrayOf(
                        Tree(
                            value = 6,
                            children = arrayOf(
                                Tree(8),
                                Tree(9, Tree(10))
                            )
                        ),
                        Tree(value = 7)
                    )
                )
            )
        )

        binaryTree = BinaryTree(
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
    }

    @Test
    fun getDepth() {
        assertTrue(TreeUtils.getDepth(tree1) == 5)
    }

    @Test
    fun preOrderAccess() {
        val expected = listOf(1, 2, 4, 5, 3, 6, 8, 9, 10, 7)
        val result = mutableListOf<Int>()
        tree1.preOrderAccess {
            result.add(it.value)
        }
        assertTrue(expected.joinToString() == result.joinToString())
    }

    @Test
    fun postOrderAccess() {
        val expected = listOf(1, 2, 4, 5, 3, 6, 8, 9, 10, 7)
        val result = mutableListOf<Int>()
        tree1.preOrderAccess {
            result.add(it.value)
        }
        println(result.joinToString())
        assertTrue(expected.joinToString() == result.joinToString())
    }

    @Test
    fun inOrderAccess() {
        val expected = listOf(4, 2, 5, 1, 6, 3, 7)
        val result = mutableListOf<Int>()
        binaryTree.inOrderAccess {
            result.add(it.value)
        }
        assertTrue(expected.joinToString() == result.joinToString())
    }

    @Test
    fun levelOrderAccess() {
        val expected = listOf(1, 2, 3, 4, 5, 6, 7)
        val result = mutableListOf<Int>()
        binaryTree.levelOrderAccess { tree, _ ->
            result.add(tree.value)
        }
        assertTrue(expected.joinToString() == result.joinToString())
    }
}