package com.lovelycatv.vertex.spider.adatper.jsoup

import com.lovelycatv.vertex.coroutines.suspendTimeoutCoroutine
import com.lovelycatv.vertex.spider.VertexSpider
import com.lovelycatv.vertex.spider.VertexSpiderOptions
import com.lovelycatv.vertex.spider.lang.HTMLDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

/**
 * A [com.lovelycatv.vertex.spider.VertexSpider] backed by jsoup. Fetches a page over HTTP and maps it into the
 * jsoup-independent [HTMLDocument] model via [JsoupHtmlMapper].
 */
class JsoupSpider(
    options: VertexSpiderOptions = VertexSpiderOptions()
) : VertexSpider(options) {

    override suspend fun fetch(url: String, delay: Long): HTMLDocument {
        val document = withContext(Dispatchers.IO) {
            Jsoup.connect(url)
                .userAgent(options.userAgent)
                .timeout(options.connectionTimeout.toInt())
                .get()
        }

        delay(delay.milliseconds)

        return JsoupHtmlMapper.toDocument(url, document)
    }

    suspend fun get(url: String, headers: Map<String, String>? = null): String? {
        fun cleanHeaderValue(value: String): String {
            return value
                .replace("\n", "")
                .replace("\r", "")
                .replace("\t", "")
                .trim()
        }

        return suspendTimeoutCoroutine(options.connectionTimeout, 10L, { null }) {
            val response = Jsoup.connect(url)
                .userAgent(options.userAgent)
                .timeout(options.connectionTimeout.toInt())
                .headers((headers ?: emptyMap()).mapValues {
                    cleanHeaderValue(it.value)
                })
                .ignoreContentType(true)
                .execute()
            it.resume(response.body())
        }
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
