package com.lovelycatv.vertex.spider.adatper.jsoup

import com.lovelycatv.vertex.spider.lang.HTMLElement
import org.jsoup.nodes.Element

/**
 * jsoup-backed [HTMLElement]. Holds the original jsoup [Element] so query methods can be
 * delegated straight to jsoup's engine — this class is the adapter, not a reimplementation.
 */
class JsoupHTMLElement(
    /**
     * The underlying jsoup element this node was mapped from.
     */
    val rawElement: Element,
    tagName: String,
    attributes: Map<String, String>,
    text: String,
    ownText: String,
) : HTMLElement(tagName, attributes, text, ownText) {

    override fun findElementsByXPath(xpath: String): List<HTMLElement> =
        rawElement.selectXpath(xpath).map { JsoupHtmlMapper.toElement(it) }

    override fun findElementByXPath(xpath: String): HTMLElement? =
        rawElement.selectXpath(xpath).map { JsoupHtmlMapper.toElement(it) }.firstOrNull()

    override fun findElementsByCssSelector(selector: String): List<HTMLElement> =
        rawElement.select(selector).map { JsoupHtmlMapper.toElement(it) }

    override fun findElementByCssSelector(selector: String): HTMLElement? =
        rawElement.selectFirst(selector)?.let { JsoupHtmlMapper.toElement(it) }
}
