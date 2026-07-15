package com.lovelycatv.vertex.spider.adatper.selenium.chromium

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
    DevToolsResponseInterceptorApplication {
    // Lazy: only touch CDP/DevTools when a ResponseInterceptor is actually registered. Eagerly
    // resolving driver.devTools breaks remote drivers (and is pointless for plain scraping).
    protected val devTools by lazy { driver.devTools }
    private var responseListenerAttached = false

    override suspend fun fetch(url: String, delay: Long): HTMLDocument {
        val responseInterceptors = interceptors.filterIsInstance<ResponseInterceptor>()
        if (responseInterceptors.isNotEmpty()) {
            refreshCdpNetworkSession(devTools)
            if (!responseListenerAttached) {
                attachResponseListener(devTools) { interceptors.filterIsInstance<ResponseInterceptor>() }
                responseListenerAttached = true
            }
        }

        return super.fetch(url, delay)
    }
}