package com.lovelycatv.vertex.spider.adatper.jsoup

import com.lovelycatv.vertex.spider.lang.HTMLComment
import com.lovelycatv.vertex.spider.lang.HTMLDocument
import com.lovelycatv.vertex.spider.lang.HTMLNode
import com.lovelycatv.vertex.spider.lang.HTMLText
import org.jsoup.nodes.*

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
        is Element -> toElement(node)
        is TextNode -> if (node.isBlank) null else HTMLText(node.text())
        is Comment -> HTMLComment(node.data)
        else -> null
    }

    /**
     * Maps a jsoup [element] (and its subtree) into a [JsoupHTMLElement], keeping a reference
     * to the original element so XPath/other queries can be delegated back to jsoup.
     */
    fun toElement(element: Element): JsoupHTMLElement {
        val htmlElement = JsoupHTMLElement(
            rawElement = element,
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
