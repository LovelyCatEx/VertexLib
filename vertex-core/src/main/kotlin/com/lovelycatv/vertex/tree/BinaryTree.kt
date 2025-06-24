package com.lovelycatv.vertex.tree

/**
 * @author lovelycat
 * @since 2025-06-25 04:35
 * @version 1.0
 */
class BinaryTree<T>(
    value: T,
    left: Tree<T>? = null,
    right: Tree<T>? = null
) : Tree<T>(value, *arrayOf(left, right)) {
    val left: BinaryTree<T>? get() = super.getOrNull(0)?.toBinaryTree()
    val right: BinaryTree<T>? get() = super.getOrNull(1)?.toBinaryTree()

    override val size: Int
        get() = if (super.size > 2) 2 else super.size
    override val validSize: Int
        get() = arrayOf(left, right).count { it != null }

    fun inOrderAccess(fx: (BinaryTree<T>) -> Unit) {
        TreeUtils.inOrderAccess(this) { fx.invoke(it.toBinaryTree()) }
    }

    fun isFullBinaryTree(): Boolean {
        var flag = true
        this.preOrderAccess {
            if (it.validSize != 0 && it.validSize != 2) {
                flag = false
                return@preOrderAccess
            }
        }
        return flag
    }

    fun isCompleteBinaryTree(): Boolean {
        if (this.isFullBinaryTree()) return true

        var flag = true
        var endOfLeaves = false
        val depth = super.depth

        this.levelOrderAccess { tree, level ->
            // 1. Except the leaves, other part of the tree should be a full binary tree
            if (level < depth - 1 && tree.size != 2) {
                flag = false
                return@levelOrderAccess
            } else if (level == depth - 1) {
                // 2. All leaves should as far left as possible
                if (endOfLeaves) {
                    if (tree.getOrNull(0) != null || tree.getOrNull(1) != null) {
                        flag = false
                        return@levelOrderAccess
                    }
                } else if (tree.getOrNull(0) == null) {
                    endOfLeaves = true
                    if (tree.getOrNull(1) != null) {
                        flag = false
                        return@levelOrderAccess
                    }
                } else if (tree.getOrNull(1) == null) {
                    endOfLeaves = true
                }
            }
        }

        return flag
    }
}