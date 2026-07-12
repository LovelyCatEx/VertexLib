package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.VertexSpider
import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupHtmlMapper
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.SeleniumInterceptor
import com.lovelycatv.vertex.spider.lang.HTMLDocument
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.openqa.selenium.Capabilities
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.remote.RemoteWebDriver
import java.io.File
import java.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * A [com.lovelycatv.vertex.spider.VertexSpider] backed by Selenium. Loads a page in a real browser (Chrome by default)
 * and, once rendering finishes, snapshots the page HTML and parses it off-browser into the
 * framework-agnostic [HTMLDocument] model (via jsoup). Selenium only drives/renders; parsing is
 * done on a static string so it can never hit StaleElementReferenceException.
 *
 * Override [createDriver] to plug in a different browser/driver.
 */
@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
abstract class SeleniumSpider<D: RemoteWebDriver, C: Capabilities>(
    protected val seleniumOptions: SeleniumSpiderOptions,
    protected val webDriverOptions: (SeleniumSpiderOptions) -> C,
) : VertexSpider(seleniumOptions) {
    protected val interceptors: MutableList<SeleniumInterceptor> = mutableListOf()

    val driver by lazy { createDriver(webDriverOptions.invoke(seleniumOptions)) }

    protected abstract fun createDriver(options: C): D

    override suspend fun fetch(url: String, delay: Long): HTMLDocument = withContext(Dispatchers.IO) {
        try {
            driver.manage()
                .timeouts()
                .pageLoadTimeout(Duration.ofMillis(seleniumOptions.pageLoadTimeout))

            driver.get(url)

            delay(delay.milliseconds)

            JsoupHtmlMapper.toDocument(url, Jsoup.parse(capturePageSource(driver), url))
        } catch (e: Exception) {
            driver.quit()
            throw e
        }
    }

    private fun capturePageSource(driver: WebDriver): String {
        val js = driver as JavascriptExecutor
        var lastError: WebDriverException? = null
        repeat(SNAPSHOT_MAX_ATTEMPTS) {
            try {
                waitForReadyState(js, options.connectionTimeout)
                val html = js.executeScript("return document.documentElement.outerHTML") as? String
                if (!html.isNullOrEmpty()) {
                    return html
                }
            } catch (e: WebDriverException) {
                lastError = e
            }
            Thread.sleep(SNAPSHOT_RETRY_DELAY_MS)
        }
        return driver.pageSource ?: throw (lastError ?: IllegalStateException("Could not fetch pageSource"))
    }

    private fun waitForReadyState(js: JavascriptExecutor, timeoutMs: Long) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            val ready = try {
                js.executeScript("return document.readyState")
            } catch (_: WebDriverException) {
                null
            }
            if (ready == "complete") {
                return
            }
            Thread.sleep(100)
        }
    }

    fun takeScreenshot(outputFile: File): File {
        return this.driver
            .getScreenshotAs(OutputType.FILE)
            .copyTo(outputFile, true)
    }

    fun takeScreenshotAsBase64(): String {
        return this.driver.getScreenshotAs(OutputType.BASE64)
    }

    fun takeScreenshotAsByteArray(): ByteArray {
        return this.driver.getScreenshotAs(OutputType.BYTES)
    }

    private companion object {
        const val SNAPSHOT_MAX_ATTEMPTS = 5
        const val SNAPSHOT_RETRY_DELAY_MS = 300L
    }

    fun addInterceptor(interceptor: SeleniumInterceptor) {
        interceptors.add(interceptor)
    }

    fun removeInterceptor(interceptor: SeleniumInterceptor) {
        interceptors.remove(interceptor)
    }


    fun quit() = this.driver.quit()
}
