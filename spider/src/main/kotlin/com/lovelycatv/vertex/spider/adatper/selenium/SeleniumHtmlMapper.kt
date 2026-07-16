package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.lang.HTMLComment
import com.lovelycatv.vertex.spider.lang.HTMLDocument
import com.lovelycatv.vertex.spider.lang.HTMLNode
import com.lovelycatv.vertex.spider.lang.HTMLText
import org.jsoup.Jsoup
import org.jsoup.nodes.Comment
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * Converts a live Selenium page into the framework-agnostic [HTMLDocument] tree — the Selenium
 * counterpart of [com.lovelycatv.vertex.spider.adatper.jsoup.JsoupHtmlMapper].
 *
 * jsoup does the reliable, static parse (attributes, text/ownText, comments — none of which
 * Selenium exposes cheaply), while the matching live [WebElement] tree is walked in lockstep so
 * every [SeleniumHTMLElement] carries a real, clickable handle. Both engines see the same DOM in
 * the same document order, so pairing element-children by position lines them up exactly.
 */
object SeleniumHtmlMapper {

    /**
     * Snapshots the [driver]'s current page and builds an [HTMLDocument] whose elements each hold
     * their live [WebElement].
     */
    fun toDocument(driver: WebDriver): HTMLDocument {
        val document = Jsoup.parse(driver.pageSource ?: "", driver.currentUrl ?: "")
        val htmlDocument = HTMLDocument(
            url = driver.currentUrl ?: "",
            title = document.title(),
            text = document.text(),
        )
        val rootElements = driver.findElements(By.xpath("/*"))
        convertChildren(document.childNodes(), rootElements).forEach { htmlDocument.appendChild(it) }
        return htmlDocument
    }

    /**
     * Maps a single live [webElement] (and its subtree) into a [SeleniumHTMLElement], parsing its
     * `outerHTML` with jsoup for structure. Used for the results of [SeleniumHTMLElement.findElementsByXPath].
     */
    fun toElement(webElement: WebElement): SeleniumHTMLElement {
        val outerHtml = webElement.getDomProperty("outerHTML").orEmpty()
        val element = Jsoup.parseBodyFragment(outerHtml).body().firstElementChild()
            ?: Jsoup.parse(outerHtml).body().firstElementChild()!!
        return mapElement(element, webElement)
    }

    private fun mapElement(element: Element, webElement: WebElement): SeleniumHTMLElement {
        val htmlElement = SeleniumHTMLElement(
            rawElement = webElement,
            tagName = element.tagName(),
            attributes = element.attributes().associate { it.key to it.value },
            text = element.text(),
            ownText = element.ownText(),
        )
        val childWebElements = webElement.findElements(By.xpath("./*"))
        convertChildren(element.childNodes(), childWebElements).forEach { htmlElement.appendChild(it) }
        return htmlElement
    }

    /**
     * Walks the jsoup [childNodes] in document order, pairing each element child with the
     * positionally-matching live element from [webElementChildren]; text and comment nodes carry
     * no handle. Node kinds we do not model (doctype, blank whitespace, ...) are dropped.
     */
    private fun convertChildren(childNodes: List<org.jsoup.nodes.Node>, webElementChildren: List<WebElement>): List<HTMLNode> {
        val result = mutableListOf<HTMLNode>()
        var elementIndex = 0
        for (child in childNodes) {
            when (child) {
                is Element -> webElementChildren.getOrNull(elementIndex++)?.let {
                    result += mapElement(child, it)
                }
                is TextNode -> if (!child.isBlank) result += HTMLText(child.text())
                is Comment -> result += HTMLComment(child.data)
            }
        }
        return result
    }
}
