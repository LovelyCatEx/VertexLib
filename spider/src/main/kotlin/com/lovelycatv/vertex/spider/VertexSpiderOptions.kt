package com.lovelycatv.vertex.spider

/**
 * Tunables shared by every [VertexSpider]. Open so backend adapters can extend it with their
 * own settings (see the Selenium adapter's options).
 */
open class VertexSpiderOptions(
    /**
     * `User-Agent` header sent with requests.
     */
    val userAgent: String = UserAgent.chromeOnWindows().toString(),
    /**
     * Connection timeout in milliseconds.
     */
    val connectionTimeout: Long = 10000L,
)
