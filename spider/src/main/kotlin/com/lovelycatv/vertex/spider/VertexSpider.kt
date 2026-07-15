package com.lovelycatv.vertex.spider

import com.lovelycatv.vertex.spider.lang.HTMLDocument

/**
 * Base type for crawlers. A spider turns a URL into a parsed [HTMLDocument] tree.
 */
abstract class VertexSpider(
    protected open val options: VertexSpiderOptions = VertexSpiderOptions()
) {
    /**
     * Fetches [url] and returns its parsed document.
     */
    abstract suspend fun fetch(url: String, delay: Long = 0L): HTMLDocument

    abstract suspend fun get(url: String, options: RequestOptions? = null): String
}
