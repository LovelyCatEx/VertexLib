package com.lovelycatv.vertex.spider

import com.lovelycatv.vertex.spider.lang.HTMLDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

/**
 * A [VertexSpider] backed by jsoup. Fetches a page over HTTP and maps it into the
 * jsoup-independent [HTMLDocument] model via [JsoupHtmlMapper].
 */
class JsoupSpider(
    options: VertexSpiderOptions = VertexSpiderOptions()
) : VertexSpider(options) {

    override suspend fun fetch(url: String): HTMLDocument {
        val document = withContext(Dispatchers.IO) {
            Jsoup.connect(url)
                .userAgent(options.userAgent)
                .timeout(options.connectionTimeout.toInt())
                .get()
        }
        return JsoupHtmlMapper.toDocument(url, document)
    }

    /**
     * Parses a raw HTML string into an [HTMLDocument] without any network access.
     * [url] is only used as the document's base location.
     */
    fun parse(html: String, url: String = ""): HTMLDocument {
        val document = Jsoup.parse(html, url)
        return JsoupHtmlMapper.toDocument(url, document)
    }
}
