package com.lovelycatv.vertex.tree

/**
 * @author lovelycat
 * @since 2025-06-25 04:27
 * @version 1.0
 */
open class Tree<T>(var value: T, vararg children: Tree<T>? = arrayOf()) {
    private val _children: MutableList<Tree<T>?> = mutableListOf()

    /**
     * Children including null
     */
    val children: List<Tree<T>?> get() = this._children

    /**
     * Children excluding null
     */
    val validChildren: List<Tree<T>> get() = this.children.filterNotNull()

    /**
     * Size of children, including null.
     */
    open val size: Int get() = this._children.size

    /**
     * Size of children, excluding null.
     */
    open val validSize: Int get() = this._children.count { it != null }

    /**
     * Depth of the tree.
     */
    val depth: Int get() = TreeUtils.getDepth(this)

    init {
        this._children.addAll(children)
    }

    fun addChild(index: Int, child: Tree<T>) {
        this._children.add(index, child)
    }

    fun addChild(child: Tree<T>) {
        this._children.add(child)
    }

    /**
     * Remove a child node.
     *
     * Dut to the children of this node have fixed positions,
     * deleting a child at a specified location will not cause
     * changes in the positions of other children.
     * NULL will replace the node you want to delete.
     *
     * If you want to delete the specified position exactly,
     * see [waveRemove]
     *
     * @param index Index of target
     * @return Deleted Node
     */
    fun remove(index: Int): Tree<T>? {
        val target = this.children.getOrNull(index)
        if (target != null) {
            this._children[index] = null
        }
        return target
    }

    /**
     * Remove a child node.
     *
     * Dut to the children of this node have fixed positions,
     * deleting a child at a specified location will also delete
     * the specified position.
     *
     * If you do not want to delete the specified position exactly,
     * see [remove]
     *
     * @param index Index of target
     * @return Deleted Node
     */
    fun waveRemove(index: Int): Tree<T>? {
        val target = this.children.getOrNull(index)
        if (index >= 0 && index < this._children.size) {
            this._children.removeAt(index)
        }
        return target
    }

    fun preOrderAccess(fx: (Tree<T>) -> Unit) {
        TreeUtils.preOrderAccess(this, fx)
    }

    fun postOrderAccess(fx: (Tree<T>) -> Unit) {
        TreeUtils.postOrderAccess(this, fx)
    }

    fun levelOrderAccess(fx: (Tree<T>, level: Int) -> Unit) {
        TreeUtils.levelOrderAccess(this, fx)
    }

    fun getOrNull(index: Int): Tree<T>? {
        return this._children.getOrNull(index)
    }

    operator fun get(index: Int): Tree<T> {
        return this._children[index] ?: throw IndexOutOfBoundsException("Index: $index, Size: ${this.size}")
    }

    operator fun set(index: Int, value: Tree<T>) {
        this._children[index] = value
    }
}