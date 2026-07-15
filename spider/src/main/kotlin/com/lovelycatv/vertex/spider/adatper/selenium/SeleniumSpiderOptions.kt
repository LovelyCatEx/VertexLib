package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.UserAgent
import com.lovelycatv.vertex.spider.VertexSpiderOptions

/**
 * Selenium-specific [com.lovelycatv.vertex.spider.VertexSpiderOptions]. Optionally pins the browser driver binary.
 */
class SeleniumSpiderOptions(
    userAgent: String = UserAgent.chromeOnWindows().toString(),
    connectionTimeout: Long = 10000L,
    /**
     * Absolute path to the chromedriver executable. When `null`, nothing is pinned and the
     * driver is left to Selenium's own resolution (Selenium Manager / PATH).
     */
    val driverPath: String? = null,
    val pageLoadTimeout: Long = 30000L,
) : VertexSpiderOptions(userAgent, connectionTimeout)
