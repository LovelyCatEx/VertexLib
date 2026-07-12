package com.lovelycatv.vertex.spider.adatper.selenium.chromium.edge

import com.lovelycatv.vertex.spider.adatper.selenium.SeleniumSpiderOptions
import com.lovelycatv.vertex.spider.adatper.selenium.chromium.RemoteChromiumSeleniumSpider
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