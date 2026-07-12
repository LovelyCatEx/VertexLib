package com.lovelycatv.vertex.spider.adatper.selenium.chromium.edge

import com.lovelycatv.vertex.spider.adatper.selenium.SeleniumSpiderOptions
import com.lovelycatv.vertex.spider.adatper.selenium.chromium.RemoteChromiumSeleniumSpider
import com.lovelycatv.vertex.spider.adatper.selenium.chromium.chrome.ChromeSeleniumSpider
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.edge.EdgeOptions
import java.net.URL

class RemoteEdgeSeleniumSpider(
    url: URL,
    seleniumOptions: SeleniumSpiderOptions,
) : RemoteChromiumSeleniumSpider<EdgeOptions>(
    url,
    seleniumOptions,
    EdgeSeleniumSpider.OPTIONS_TRANSFORMER
)