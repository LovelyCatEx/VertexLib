package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.lang.HTMLDocument
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * Converts a live Selenium DOM (through a [WebDriver]) into the framework-agnostic
 * [HTMLDocument] tree. The single boundary between Selenium and
 * [com.lovelycatv.vertex.spider.lang].
 *
 * Selenium exposes a live element handle rather than a static node list, so only element
 * nodes are reconstructed (traversed via the child-element axis); text is captured through
 * `getText` / own-text.
 */
class SeleniumHtmlMapper(private val driver: WebDriver) {

    private val js = driver as JavascriptExecutor

    /**
     * Builds an [HTMLDocument] from the currently loaded page, tagging it with [url].
     */
    fun toDocument(url: String): HTMLDocument {
        val root = driver.findElement(By.tagName("html"))
        val document = HTMLDocument(
            url = url,
            title = driver.title ?: "",
            text = root.text ?: "",
        )
        document.appendChild(toElement(root))
        return document
    }

    /**
     * Maps a Selenium [element] (and its element subtree) into a [SeleniumHTMLElement],
     * keeping the live handle so further queries can be delegated back to Selenium.
     */
    fun toElement(element: WebElement): SeleniumHTMLElement {
        val htmlElement = SeleniumHTMLElement(
            rawElement = element,
            mapper = this,
            tagName = element.tagName ?: "",
            attributes = attributesOf(element),
            text = element.text ?: "",
            ownText = ownTextOf(element),
        )
        for (child in element.findElements(By.xpath("./*"))) {
            htmlElement.appendChild(toElement(child))
        }
        return htmlElement
    }

    /**
     * Reads all attributes of [element] via a small JS snippet (Selenium has no native
     * "get all attributes" call).
     */
    private fun attributesOf(element: WebElement): Map<String, String> {
        @Suppress("UNCHECKED_CAST")
        val raw = js.executeScript(
            "var items = {};" +
                "for (var i = 0; i < arguments[0].attributes.length; i++) {" +
                "  items[arguments[0].attributes[i].name] = arguments[0].attributes[i].value;" +
                "}" +
                "return items;",
            element
        ) as? Map<String, Any?> ?: emptyMap()
        return raw.mapValues { (_, v) -> v?.toString() ?: "" }
    }

    /**
     * Text owned directly by [element] (its direct text-node children), excluding descendants.
     */
    private fun ownTextOf(element: WebElement): String {
        val raw = js.executeScript(
            "var t = '';" +
                "arguments[0].childNodes.forEach(function(n){ if (n.nodeType === 3) t += n.textContent; });" +
                "return t;",
            element
        ) as? String ?: ""
        return raw.trim()
    }
}
