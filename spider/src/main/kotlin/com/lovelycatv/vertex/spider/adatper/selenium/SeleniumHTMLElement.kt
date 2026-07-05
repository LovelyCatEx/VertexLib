package com.lovelycatv.vertex.spider

import com.lovelycatv.vertex.spider.lang.HTMLElement
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

/**
 * Selenium-backed [HTMLElement]. Holds the live [WebElement] so queries delegate straight to
 * the WebDriver — this is the adapter, the same idea as [JsoupHTMLElement] but for Selenium.
 */
class SeleniumHTMLElement(
    /**
     * The underlying Selenium element this node was mapped from.
     */
    val rawElement: WebElement,
    private val mapper: SeleniumHtmlMapper,
    tagName: String,
    attributes: Map<String, String>,
    text: String,
    ownText: String,
) : HTMLElement(tagName, attributes, text, ownText) {

    override fun findByXPath(xpath: String): List<HTMLElement> =
        rawElement.findElements(By.xpath(xpath)).map { mapper.toElement(it) }
}
