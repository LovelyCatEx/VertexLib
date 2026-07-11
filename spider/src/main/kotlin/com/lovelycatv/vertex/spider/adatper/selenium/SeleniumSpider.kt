package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.HTTPStatus
import com.lovelycatv.vertex.spider.VertexSpider
import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupHtmlMapper
import com.lovelycatv.vertex.spider.lang.HTMLDocument
import org.jsoup.Jsoup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chromium.ChromiumOptions
import org.openqa.selenium.devtools.Command
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.devtools.v150.network.Network
import java.time.Duration
import java.util.Optional

/**
 * A [com.lovelycatv.vertex.spider.VertexSpider] backed by Selenium. Loads a page in a real browser (Chrome by default)
 * and, once rendering finishes, snapshots the page HTML and parses it off-browser into the
 * framework-agnostic [HTMLDocument] model (via jsoup). Selenium only drives/renders; parsing is
 * done on a static string so it can never hit StaleElementReferenceException.
 *
 * Override [createDriver] to plug in a different browser/driver.
 */
@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
abstract class SeleniumSpider<D: WebDriver, O: ChromiumOptions<O>>(
    protected val seleniumOptions: SeleniumSpiderOptions,
    protected val webDriverOptions: O,
) : VertexSpider(seleniumOptions) {
    private val interceptors: MutableList<SeleniumInterceptor> = mutableListOf()

    val driver by lazy { createDriver(webDriverOptions) }

    abstract val devTools: DevTools?

    protected abstract fun createDriver(options: O): D

    override suspend fun fetch(url: String): HTMLDocument = withContext(Dispatchers.IO) {
        try {
            val responseInterceptors = interceptors.filterIsInstance<ResponseInterceptor>()
            if (devTools != null && responseInterceptors.isNotEmpty()) {
                devTools?.createSession()
                // Network.enable is a Command<Void>; keeping that static type makes Kotlin emit a
                // `checkcast Void` on send()'s return value, which throws when CDP replies with a
                // (non-empty) result body. Widen to Command<*> so no Void cast is generated.
                val enableNetwork: Command<*> = Network.enable(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
                )
                devTools?.send(enableNetwork)

                devTools?.addListener(Network.responseReceived()) { resp ->
                    responseInterceptors.forEach {
                        it.intercept(
                            RemoteResponse(
                                timestamp = (resp.timestamp.toString().toDouble() * 1_000_000L).toLong(),
                                url = resp.response.url,
                                status = HTTPStatus.fromCode(resp.response.status),
                            )
                        )
                    }
                }
            }

            driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(options.connectionTimeout))
            driver.get(url)
            JsoupHtmlMapper.toDocument(url, Jsoup.parse(driver.pageSource ?: "", url))
        } catch (e: Exception) {
            driver.quit()
            throw e
        }
    }

    fun addInterceptor(interceptor: SeleniumInterceptor) {
        interceptors.add(interceptor)
    }

    fun removeInterceptor(interceptor: SeleniumInterceptor) {
        interceptors.remove(interceptor)
    }


    fun quit() = this.driver.quit()
}
