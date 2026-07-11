package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.log.logger
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chromium.ChromiumOptions
import org.openqa.selenium.devtools.DevTools

class ChromeSeleniumSpider(
    seleniumOptions: SeleniumSpiderOptions,
    webDriverOptions: ChromeOptions
) : SeleniumSpider<ChromeDriver, ChromeOptions>(seleniumOptions, webDriverOptions) {
    private val logger = logger()

    override val devTools: DevTools = driver.devTools

    override fun createDriver(options: ChromeOptions): ChromeDriver {
        seleniumOptions.driverPath?.let {
            System.setProperty("webdriver.chrome.driver", it)
            logger.info("WebDriver path: $it")
        }

        return ChromeDriver(options)
    }
}