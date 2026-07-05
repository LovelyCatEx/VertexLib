package com.lovelycatv.vertex.spider.lang

/**
 * An HTML comment node, e.g. `<!-- hello -->`.
 */
class HTMLComment(
    /**
     * Raw comment content, without the surrounding `<!--` `-->` markers.
     */
    val data: String,
) : AbstractHTMLNode() {
    override val nodeType: HTMLNodeType
        get() = HTMLNodeType.COMMENT

    override val text: String
        get() = data

    override val value: String
        get() = data

    override fun toString(): String = "<!--$data-->"
}
