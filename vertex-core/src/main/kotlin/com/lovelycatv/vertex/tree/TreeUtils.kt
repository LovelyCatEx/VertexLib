package com.lovelycatv.vertex.tree

import java.util.*


object TreeUtils {
    @JvmStatic
    fun <T> getDepth(root: Tree<T>?): Int {
        if (root == null) return 0
        var maxChildDepth = 0
        for (child in root.validChildren) {
            maxChildDepth = maxChildDepth.coerceAtLeast(getDepth(child))
        }
        return maxChildDepth + 1
    }

    @JvmStatic
    fun <T> preOrderAccess(root: Tree<T>?, fx: (Tree<T>) -> Unit) {
        if (root == null) return

        fx.invoke(root)

        for (child in root.children) {
            preOrderAccess(child, fx)
        }
    }

    @JvmStatic
    fun <T> postOrderAccess(root: Tree<T>?, fx: (Tree<T>) -> Unit) {
        if (root == null) return

        for (child in root.children) {
            postOrderAccess(child, fx)
        }

        fx.invoke(root)
    }

    @JvmStatic
    fun <T> inOrderAccess(root: Tree<T>?, fx: (Tree<T>) -> Unit) {
        if (root == null) return
        if (root.size > 2) {
            throw IllegalArgumentException("In Order Access only supports binary trees (which its maximum size of children is 2)")
        }

        inOrderAccess(root.getOrNull(0), fx)
        fx.invoke(root)
        inOrderAccess(root.getOrNull(1), fx)
    }

    @JvmStatic
    fun <T> levelOrderAccess(root: Tree<T>?, fx: (Tree<T>, level: Int) -> Unit) {
        if (root == null) return

        val queue: Queue<Tree<T>> = LinkedList()
        queue.offer(root)

        var currentLevel = 1
        var currentLevelSize = 1
        var nextLevelChildrenSize = 0

        while (!queue.isEmpty()) {
            val current = queue.poll()
            currentLevelSize--

            fx.invoke(current, currentLevel)

            nextLevelChildrenSize += current.validSize

            if (currentLevelSize == 0) {
                currentLevel++
                currentLevelSize = nextLevelChildrenSize
                nextLevelChildrenSize = 0
            }

            for (child in current.children) {
                if (child != null) {
                    queue.offer(child)
                }
            }
        }
    }
}