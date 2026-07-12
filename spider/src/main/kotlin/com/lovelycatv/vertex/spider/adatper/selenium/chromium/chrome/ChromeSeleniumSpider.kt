package com.lovelycatv.vertex.spider.adatper.selenium.chromium.chrome

import com.lovelycatv.vertex.spider.adatper.selenium.SeleniumSpiderOptions
import com.lovelycatv.vertex.spider.adatper.selenium.chromium.ChromiumSeleniumSpider
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

class ChromeSeleniumSpider(
    seleniumOptions: SeleniumSpiderOptions,
) : ChromiumSeleniumSpider<ChromeDriver, ChromeOptions>(
    seleniumOptions,
    OPTIONS_TRANSFORMER
) {
    override fun createDriver(options: ChromeOptions): ChromeDriver {
        return ChromeDriver(options)
    }

    companion object {
        val OPTIONS_TRANSFORMER = { options: SeleniumSpiderOptions ->
            options.driverPath?.let {
                System.setProperty("webdriver.chrome.driver", it)
            }

            ChromeOptions().apply {
                addArguments("--disable-dev-shm-usage")
                addArguments("--no-sandbox")
                addArguments("--user-agent=${options.userAgent}")
                addArguments("--window-size=1920,1080")
                addArguments("--disable-gpu")
            }
        }
    }
}