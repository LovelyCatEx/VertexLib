package com.lovelycatv.vertex.tree

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class TreeTest {
    companion object {
        private lateinit var tree1:Tree<Int>
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
                        Tree(value = 6),
                        Tree(value = 7)
                    )
                )
            )
        )
    }

    @Test
    fun getChildren() {
        assertTrue(tree1[0].value == 2)
        assertTrue(tree1[1].value == 3)
    }

    @Test
    fun getSize() {
        assertTrue(tree1.size == 2)
    }

    @Test
    fun getDepth() {
        assertTrue(tree1.depth == 3)
    }

    @Test
    fun remove() {
        tree1.remove(1)
        assertTrue(tree1.size == 2)
        assertTrue(tree1.validSize == 1)
    }

    @Test
    fun waveRemove() {
        tree1.waveRemove(0)
        assertTrue(tree1.size == 1)
    }

    @Test
    fun preOrderAccess() {
        val expected = listOf(1, 2, 4, 5, 3, 6, 7)
        val result = mutableListOf<Int>()
        tree1.preOrderAccess {
            result.add(it.value)
        }
        assertTrue(expected.joinToString() == result.joinToString())
    }

    @Test
    fun postOrderAccess() {
        val expected = listOf(4, 5, 2, 6, 7, 3, 1)
        val result = mutableListOf<Int>()
        tree1.postOrderAccess {
            result.add(it.value)
        }
        assertTrue(expected.joinToString() == result.joinToString())
    }

    @Test
    fun levelOrderAccess() {
        val expected = listOf(1, 2, 3, 4, 5, 6, 7)
        val result = mutableListOf<Int>()
        tree1.levelOrderAccess { tree, _ ->
            result.add(tree.value)
        }
        assertTrue(expected.joinToString() == result.joinToString())
    }

    @Test
    fun get() {
        assertTrue(tree1[0].value == 2)
        assertTrue(tree1[0][1].value == 5)
    }

    @Test
    fun set() {
        tree1[0] = Tree(200, Tree(400), Tree(500))
        assertTrue(tree1[0].value == 200)
    }
}