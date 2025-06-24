package com.lovelycatv.vertex.tree

/**
 * @author lovelycat
 * @since 2025-06-25 05:36
 * @version 1.0
 */
class TreeExtensions private constructor()

fun <T> Tree<T>.toBinaryTree() = BinaryTree(
    value = this.value,
    left = this.getOrNull(0),
    right = this.getOrNull(1)
)