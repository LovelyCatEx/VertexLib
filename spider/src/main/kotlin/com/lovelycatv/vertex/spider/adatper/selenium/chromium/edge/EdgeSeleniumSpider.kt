package com.lovelycatv.vertex.spider.adatper.selenium.chromium.edge

import com.lovelycatv.vertex.spider.adatper.selenium.SeleniumSpiderOptions
import com.lovelycatv.vertex.spider.adatper.selenium.chromium.ChromiumSeleniumSpider
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions

class EdgeSeleniumSpider(
    seleniumOptions: SeleniumSpiderOptions,
) : ChromiumSeleniumSpider<EdgeDriver, EdgeOptions>(
    seleniumOptions,
    OPTIONS_TRANSFORMER
) {
    override fun createDriver(options: EdgeOptions): EdgeDriver {
        return EdgeDriver(options)
    }

    companion object {
        val OPTIONS_TRANSFORMER = { options: SeleniumSpiderOptions ->
            EdgeOptions().apply {
                addArguments("--disable-dev-shm-usage")
                addArguments("--no-sandbox")
                addArguments("--user-agent=${options.userAgent}")
                addArguments("--window-size=1920,1080")
                addArguments("--disable-gpu")
            }
        }
    }
}