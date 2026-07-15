package com.lovelycatv.vertex.spider.adatper.selenium.chromium

import com.lovelycatv.vertex.spider.adatper.selenium.RemoteRequest
import com.lovelycatv.vertex.spider.adatper.selenium.RemoteResponse
import com.lovelycatv.vertex.spider.adatper.selenium.SeleniumSpider
import com.lovelycatv.vertex.spider.adatper.selenium.SeleniumSpiderOptions
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.ResponseInterceptor
import com.lovelycatv.vertex.spider.lang.HTMLDocument
import org.openqa.selenium.chromium.ChromiumDriver
import org.openqa.selenium.chromium.ChromiumOptions

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
abstract class ChromiumSeleniumSpider<D: ChromiumDriver, O: ChromiumOptions<O>>(
    seleniumOptions: SeleniumSpiderOptions,
    webDriverOptions: (SeleniumSpiderOptions) -> O
) : SeleniumSpider<D, O>(seleniumOptions, webDriverOptions),
    DevToolsRequestResponseInterceptorApplication {
    // Lazy: only touch CDP/DevTools when a ResponseInterceptor is actually registered. Eagerly
    // resolving driver.devTools breaks remote drivers (and is pointless for plain scraping).
    protected val devTools by lazy { driver.devTools }
    private var responseListenerAttached = false

    override val requestRecords: MutableMap<String, RemoteRequest> = mutableMapOf()
    override val responseRecords: MutableMap<String, RemoteResponse> = mutableMapOf()

    override suspend fun fetch(url: String, delay: Long): HTMLDocument {
        refreshCdpNetworkSession(devTools)
        if (!responseListenerAttached) {
            attachListener(devTools) { interceptors }
            responseListenerAttached = true
        }

        return super.fetch(url, delay)
    }
}