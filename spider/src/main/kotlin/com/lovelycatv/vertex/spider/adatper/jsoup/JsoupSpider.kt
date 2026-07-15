package com.lovelycatv.vertex.spider.adatper.jsoup

import com.lovelycatv.vertex.coroutines.suspendTimeoutCoroutine
import com.lovelycatv.vertex.spider.RequestOptions
import com.lovelycatv.vertex.spider.VertexSpider
import com.lovelycatv.vertex.spider.lang.HTMLDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

/**
 * A [com.lovelycatv.vertex.spider.VertexSpider] backed by jsoup. Fetches a page over HTTP and maps it into the
 * jsoup-independent [HTMLDocument] model via [JsoupHtmlMapper].
 */
class JsoupSpider(
    val spiderOptions: JsoupSpiderOptions = JsoupSpiderOptions()
) : VertexSpider(spiderOptions) {

    override suspend fun fetch(url: String, delay: Long): HTMLDocument {
        val document = withContext(Dispatchers.IO) {
            val conn = Jsoup.connect(url)
                .userAgent(spiderOptions.userAgent)
                .timeout(spiderOptions.connectionTimeout.toInt())
                .headers(spiderOptions.requestOptions.plainHeaders())

            spiderOptions.requestOptions.referer?.let { conn.referrer(it) }

            conn.get()
        }

        delay(delay.milliseconds)

        return JsoupHtmlMapper.toDocument(url, document)
    }

    override suspend fun get(url: String, options: RequestOptions?): String {
        return suspendTimeoutCoroutine<String>(
            spiderOptions.connectionTimeout,
            10L,
            { throw IllegalStateException("timeout after ${spiderOptions.connectionTimeout} milliseconds") },
        ) { continuation ->
            val conn = Jsoup.connect(url)
                .userAgent(spiderOptions.userAgent)
                .timeout(spiderOptions.connectionTimeout.toInt())
                .headers((spiderOptions.requestOptions.plainHeaders() + (options?.plainHeaders() ?: emptyMap())).mapValues {
                    cleanHeaderValue(it.value)
                })
                .ignoreContentType(true)

            val referer = options?.referer ?: this.spiderOptions.requestOptions.referer
            referer?.let { conn.referrer(it) }

            val response = conn.execute()
            continuation.resume(response.body() ?: throw IllegalStateException("Remote returned null response body"))
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

    private fun cleanHeaderValue(value: String): String {
        return value
            .replace("\n", "")
            .replace("\r", "")
            .replace("\t", "")
            .trim()
    }
}
