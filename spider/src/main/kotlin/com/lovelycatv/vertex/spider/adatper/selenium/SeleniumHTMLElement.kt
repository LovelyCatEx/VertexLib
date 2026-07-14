package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.lang.HTMLElement
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * Selenium-backed [HTMLElement]. Mirrors [com.lovelycatv.vertex.spider.adatper.jsoup.JsoupHTMLElement],
 * but holds the live [WebElement] instead of a detached jsoup node — so [rawElement] can be
 * clicked, typed into, or otherwise driven against the running browser.
 *
 * Queries delegate straight to Selenium's own engine, exactly as the jsoup adapter delegates to
 * jsoup — no hand-rolled XPath, no injected JavaScript.
 */
class SeleniumHTMLElement(
    /**
     * The underlying live Selenium element this node was mapped from.
     */
    val rawElement: WebElement,
    tagName: String,
    attributes: Map<String, String>,
    text: String,
    ownText: String,
) : HTMLElement(tagName, attributes, text, ownText) {

    override fun findByXPath(xpath: String): List<HTMLElement> =
        rawElement.findElements(By.xpath(xpath)).map { SeleniumHtmlMapper.toElement(it) }
}
