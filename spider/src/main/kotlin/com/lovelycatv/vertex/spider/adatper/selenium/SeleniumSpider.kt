package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.VertexSpider
import com.lovelycatv.vertex.spider.lang.HTMLDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.time.Duration

/**
 * A [com.lovelycatv.vertex.spider.VertexSpider] backed by Selenium. Loads a page in a real browser (Chrome by default)
 * and maps the live DOM into the framework-agnostic [HTMLDocument] model via [SeleniumHtmlMapper].
 *
 * Override [createDriver] to plug in a different browser/driver.
 */
open class SeleniumSpider(
    private val seleniumOptions: SeleniumSpiderOptions = SeleniumSpiderOptions()
) : VertexSpider(seleniumOptions) {

    /**
     * Creates the [WebDriver] used for a fetch. Default is a headless Chrome carrying the
     * configured user-agent. If [SeleniumSpiderOptions.driverPath] is set it pins the
     * chromedriver; when `null` the driver is left to Selenium's own resolution.
     * The caller ([fetch]) owns the driver and quits it afterwards.
     */
    protected open fun createDriver(): WebDriver {
        seleniumOptions.driverPath?.let {
            System.setProperty("webdriver.chrome.driver", it)
        }
        val chromeOptions = ChromeOptions().apply {
            addArguments("--headless=new")
            addArguments("--user-agent=${seleniumOptions.userAgent}")
        }
        return ChromeDriver(chromeOptions)
    }

    override suspend fun fetch(url: String): HTMLDocument = withContext(Dispatchers.IO) {
        val driver = createDriver()
        try {
            driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(options.connectionTimeout))
            driver.get(url)
            SeleniumHtmlMapper(driver).toDocument(url)
        } finally {
            driver.quit()
        }
    }
}
