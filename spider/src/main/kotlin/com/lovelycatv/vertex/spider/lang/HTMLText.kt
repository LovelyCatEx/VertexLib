package com.lovelycatv.vertex.spider.lang

/**
 * A run of character data inside an element, e.g. the `Hello` in `<p>Hello</p>`.
 */
class HTMLText(
    override val text: String,
) : AbstractHTMLNode() {
    override val nodeType: HTMLNodeType
        get() = HTMLNodeType.TEXT

    override val value: String
        get() = text

    override fun toString(): String = text
}
