package com.lovelycatv.vertex.spider.lang

/**
 * The root of a parsed HTML document. Carries page-level metadata ([url], [title]) and,
 * through [AbstractHTMLNode], the whole element tree plus its traversal helpers.
 */
class HTMLDocument(
    /**
     * The location the document was fetched/parsed from, empty when unknown.
     */
    val url: String,
    /**
     * The `<title>` of the page, empty when absent.
     */
    val title: String,
    override val text: String,
    override val value: String = "",
) : AbstractHTMLNode() {
    override val nodeType: HTMLNodeType
        get() = HTMLNodeType.DOCUMENT
}
