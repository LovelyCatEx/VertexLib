package com.lovelycatv.vertex.spider

import com.lovelycatv.vertex.spider.adatper.selenium.SeleniumSpiderOptions
import com.lovelycatv.vertex.spider.adatper.selenium.chromium.chrome.RemoteChromeSeleniumSpider
import kotlinx.coroutines.runBlocking
import java.net.URI
import kotlin.test.Test

class Remote {
    @Test
    fun test() {
        val remote = RemoteChromeSeleniumSpider(
            URI.create("http://localhost:4444/wd/hub").toURL(),
            SeleniumSpiderOptions()
        )

        runBlocking {
            remote.fetch("https://baidu.com")
        }
    }
}