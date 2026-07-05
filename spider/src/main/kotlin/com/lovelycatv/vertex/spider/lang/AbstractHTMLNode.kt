package com.lovelycatv.vertex.spider.lang

/**
 * Base implementation shared by every [HTMLNode]. It owns the child list and the
 * parent back-reference, and provides the tree-traversal helpers used to query the document.
 *
 * The tree is built by the crawler layer via [appendChild]; end users only read it.
 */
abstract class AbstractHTMLNode : HTMLNode {
    override var parentNode: HTMLNode? = null
        internal set

    private val mutableChildNodes = mutableListOf<HTMLNode>()

    override val childNodes: List<HTMLNode>
        get() = mutableChildNodes

    /**
     * Appends [child] to this node and links its [parentNode] back to this node.
     * Intended for the mapping layer inside this module, not for public tree editing.
     */
    internal fun appendChild(child: HTMLNode): AbstractHTMLNode {
        mutableChildNodes.add(child)
        if (child is AbstractHTMLNode) {
            child.parentNode = this
        }
        return this
    }

    /**
     * All descendant [HTMLElement]s, depth-first, in document order.
     */
    fun descendantElements(): List<HTMLElement> {
        val result = mutableListOf<HTMLElement>()
        fun walk(node: HTMLNode) {
            for (child in node.childNodes) {
                if (child is HTMLElement) {
                    result.add(child)
                }
                walk(child)
            }
        }
        walk(this)
        return result
    }

    /**
     * The first descendant element whose `id` attribute equals [id], or `null`.
     */
    fun getElementById(id: String): HTMLElement? =
        descendantElements().firstOrNull { it.id == id }

    /**
     * All descendant elements with the given tag name (case-insensitive).
     */
    fun getElementsByTag(tagName: String): List<HTMLElement> =
        descendantElements().filter { it.tagName.equals(tagName, ignoreCase = true) }

    /**
     * All descendant elements that carry [className] among their css classes.
     */
    fun getElementsByClass(className: String): List<HTMLElement> =
        descendantElements().filter { className in it.classNames }
}
