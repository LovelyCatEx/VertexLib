package com.lovelycatv.vertex.spider.adatper.selenium.chromium.chrome

import com.lovelycatv.vertex.spider.adatper.selenium.SeleniumSpiderOptions
import com.lovelycatv.vertex.spider.adatper.selenium.chromium.RemoteChromiumSeleniumSpider
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.net.URL

class RemoteChromeSeleniumSpider(
    url: URL,
    seleniumOptions: SeleniumSpiderOptions,
) : RemoteChromiumSeleniumSpider<ChromeOptions>(
    url,
    seleniumOptions,
    ChromeSeleniumSpider.OPTIONS_TRANSFORMER
)