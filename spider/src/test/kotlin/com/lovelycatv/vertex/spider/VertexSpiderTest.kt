package com.lovelycatv.vertex.spider

import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupSpider
import com.lovelycatv.vertex.spider.lang.HTMLNodeType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class VertexSpiderTest {

    private val html = """
        <html>
            <head><title>Demo Page</title></head>
            <body>
                <div id="main" class="container primary">
                    <h1>Title</h1>
                    <p class="text">Hello <b>World</b></p>
                    <!-- a comment -->
                    <a href="https://example.com/link">link</a>
                </div>
            </body>
        </html>
    """.trimIndent()

    @Test
    fun testParseDocumentMetadata() {
        val doc = JsoupSpider().parse(html, "https://example.com")

        assertEquals(HTMLNodeType.DOCUMENT, doc.nodeType)
        assertEquals("Demo Page", doc.title)
        assertEquals("https://example.com", doc.url)
    }

    @Test
    fun testElementLookup() {
        val doc = JsoupSpider().parse(html)

        val main = doc.getElementById("main")
        assertNotNull(main)
        assertEquals("div", main!!.tagName)
        assertTrue(main.classNames.containsAll(listOf("container", "primary")))

        val paragraphs = doc.getElementsByTag("p")
        assertEquals(1, paragraphs.size)
        assertEquals("Hello World", paragraphs.first().text)

        val byClass = doc.getElementsByClass("text")
        assertEquals(1, byClass.size)

        val anchor = doc.getElementsByTag("a").first()
        assertEquals("https://example.com/link", anchor.attr("href"))
        assertEquals(HTMLNodeType.ELEMENT, anchor.nodeType)
    }

    @Test
    fun testChildParentLinks() {
        val doc = JsoupSpider().parse(html)

        val main = doc.getElementById("main")!!
        // every descendant must chain back up to the document root
        assertNotNull(main.parentNode)
        for (element in doc.descendantElements()) {
            assertNotNull(element.parentNode)
        }
    }

    @Test
    fun testFindByXPath() {
        val doc = JsoupSpider().parse(html)

        // document-level query
        val paragraphs = doc.findElementsByXPath("//p")
        assertEquals(1, paragraphs.size)
        assertEquals("Hello World", paragraphs.first().text)

        // element-relative query
        val main = doc.getElementById("main")!!
        val anchors = main.findElementsByXPath(".//a")
        assertEquals(1, anchors.size)
        assertEquals("https://example.com/link", anchors.first().attr("href"))
    }
}
