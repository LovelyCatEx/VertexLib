package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.VertexSpiderOptions

/**
 * Selenium-specific [com.lovelycatv.vertex.spider.VertexSpiderOptions]. Optionally pins the browser driver binary.
 */
class SeleniumSpiderOptions(
    userAgent: String = "Mozilla/5.0",
    connectionTimeout: Long = 10000L,
    /**
     * Absolute path to the chromedriver executable. When `null`, nothing is pinned and the
     * driver is left to Selenium's own resolution (Selenium Manager / PATH).
     */
    val driverPath: String? = null
) : VertexSpiderOptions(userAgent, connectionTimeout)
