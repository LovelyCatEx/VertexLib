package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.VertexSpider
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.SeleniumInterceptor
import com.lovelycatv.vertex.spider.lang.HTMLDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.openqa.selenium.*
import org.openqa.selenium.remote.RemoteWebDriver
import java.io.File
import java.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * A [com.lovelycatv.vertex.spider.VertexSpider] backed by Selenium. Loads a page in a real browser
 * (Chrome by default) and, once rendering finishes, builds the framework-agnostic [HTMLDocument]
 * tree via [SeleniumHtmlMapper] — every element keeps a handle to its live
 * [org.openqa.selenium.WebElement], so nodes stay clickable/driveable.
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

            // Wait for the page to finish loading, then build the tree straight from the live DOM
            // (via SeleniumHtmlMapper) so every element keeps a handle to its real WebElement and
            // stays clickable — unlike parsing a static HTML snapshot through jsoup.
            waitForReadyState(driver as JavascriptExecutor, options.connectionTimeout)

            getCurrentDocument()
        } catch (e: Exception) {
            driver.quit()
            throw e
        }
    }

    fun getCurrentDocument(): HTMLDocument {
        return SeleniumHtmlMapper.toDocument(driver)
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

    fun addInterceptor(interceptor: SeleniumInterceptor) {
        interceptors.add(interceptor)
    }

    fun removeInterceptor(interceptor: SeleniumInterceptor) {
        interceptors.remove(interceptor)
    }


    fun quit() = this.driver.quit()
}
