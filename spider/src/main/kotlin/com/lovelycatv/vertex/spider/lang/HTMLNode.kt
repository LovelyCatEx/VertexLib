package com.lovelycatv.vertex.spider.lang

/**
 * A jsoup-independent node of an HTML document tree.
 *
 * Concrete implementations: [HTMLDocument] (root), [HTMLElement], [HTMLText] and [HTMLComment].
 */
interface HTMLNode {
    /**
     * The kind of this node.
     */
    val nodeType: HTMLNodeType

    /**
     * The node this node is attached to, or `null` when it is a detached / root node.
     */
    val parentNode: HTMLNode?

    /**
     * Direct children of this node, in document order.
     */
    val childNodes: List<HTMLNode>

    /**
     * The combined, normalized text of this node and all of its descendants.
     */
    val text: String

    /**
     * The node's own value: the content for [HTMLText] / [HTMLComment], or the
     * `value` attribute for an [HTMLElement] (empty when it has none).
     */
    val value: String
}
