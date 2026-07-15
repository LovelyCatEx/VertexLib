package com.lovelycatv.vertex.spider.adatper.selenium.chromium

import com.lovelycatv.vertex.log.logger
import com.lovelycatv.vertex.spider.adatper.selenium.RemoteRequest
import com.lovelycatv.vertex.spider.adatper.selenium.RemoteResponse
import com.lovelycatv.vertex.spider.adatper.selenium.RemoteSeleniumSpider
import com.lovelycatv.vertex.spider.adatper.selenium.SeleniumSpiderOptions
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.ResponseInterceptor
import com.lovelycatv.vertex.spider.lang.HTMLDocument
import org.openqa.selenium.chromium.ChromiumOptions
import org.openqa.selenium.devtools.HasDevTools
import org.openqa.selenium.remote.RemoteWebDriver
import org.slf4j.Logger
import java.net.URL

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
abstract class RemoteChromiumSeleniumSpider<O: ChromiumOptions<O>>(
    url: URL,
    seleniumOptions: SeleniumSpiderOptions,
    webDriverOptions: (SeleniumSpiderOptions) -> O
) : RemoteSeleniumSpider<RemoteWebDriver, O>(url, seleniumOptions, webDriverOptions),
    DevToolsRequestResponseInterceptorApplication
{
    // Lazy: a remote driver can't establish a CDP/DevTools connection at construction time, and
    // plain scraping never needs it. Only resolve it when a ResponseInterceptor is registered.
    private val devTools by lazy { (driver as HasDevTools).devTools }
    private var responseListenerAttached = false

    override val logger: Logger = logger()
    override val requestRecords: MutableMap<String, RemoteRequest> = mutableMapOf()
    override val responseRecords: MutableMap<String, RemoteResponse> = mutableMapOf()

    override fun createDriver(options: O): RemoteWebDriver {
        return super.createAugmentedDriver(options)
    }

    override suspend fun fetch(url: String, delay: Long): HTMLDocument {
        refreshCdpNetworkSession(devTools)
        if (!responseListenerAttached) {
            attachListener(devTools) { interceptors }
            responseListenerAttached = true
        }

        return super.fetch(url, delay)
    }
}