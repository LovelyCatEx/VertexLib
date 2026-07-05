package com.lovelycatv.vertex.spider

import com.lovelycatv.vertex.spider.lang.HTMLComment
import com.lovelycatv.vertex.spider.lang.HTMLDocument
import com.lovelycatv.vertex.spider.lang.HTMLElement
import com.lovelycatv.vertex.spider.lang.HTMLNode
import com.lovelycatv.vertex.spider.lang.HTMLText
import org.jsoup.nodes.Comment
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Converts a jsoup [Document] into the jsoup-independent [HTMLDocument] tree used by the
 * rest of the library. This is the single boundary between jsoup and [com.lovelycatv.vertex.spider.lang].
 */
object JsoupHtmlMapper {

    /**
     * Builds an [HTMLDocument] from a parsed jsoup [document], tagging it with [url].
     */
    fun toDocument(url: String, document: Document): HTMLDocument {
        val htmlDocument = HTMLDocument(
            url = url,
            title = document.title(),
            text = document.text(),
        )
        for (child in document.childNodes()) {
            convert(child)?.let { htmlDocument.appendChild(it) }
        }
        return htmlDocument
    }

    /**
     * Converts a single jsoup [node], returning `null` for node kinds we do not model
     * (doctype, blank whitespace text, xml declarations, ...).
     */
    private fun convert(node: Node): HTMLNode? = when (node) {
        is Element -> convertElement(node)
        is TextNode -> if (node.isBlank) null else HTMLText(node.text())
        is Comment -> HTMLComment(node.data)
        else -> null
    }

    private fun convertElement(element: Element): HTMLElement {
        val htmlElement = HTMLElement(
            tagName = element.tagName(),
            attributes = element.attributes().associate { it.key to it.value },
            text = element.text(),
            ownText = element.ownText(),
        )
        for (child in element.childNodes()) {
            convert(child)?.let { htmlElement.appendChild(it) }
        }
        return htmlElement
    }
}
