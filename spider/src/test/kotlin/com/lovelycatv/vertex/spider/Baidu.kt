package com.lovelycatv.vertex.spider

import com.lovelycatv.vertex.spider.adatper.selenium.SeleniumSpiderOptions
import com.lovelycatv.vertex.spider.adatper.selenium.chromium.chrome.RemoteChromeSeleniumSpider
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.RequestInterceptor
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.ResponseBodyInterceptor
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.ResponseInterceptor
import kotlinx.coroutines.runBlocking
import java.net.URI
import java.net.URL
import kotlin.test.Test

class Baidu {
    @Test
    fun test() {
        val spider = RemoteChromeSeleniumSpider(
            url = URI.create("http://localhost:4444/wd/hub").toURL(),
            seleniumOptions = SeleniumSpiderOptions(

            )
        )

        runBlocking {
            try {
                println("prepare")
                spider.addInterceptor(
                    RequestInterceptor {
                        println(it.requestId + "=" + it.method + ": " + it.url)
                    }
                )
                spider.addInterceptor(
                    ResponseBodyInterceptor {
                        println(it.requestId + "=>" + it.response.status.code.toString() + ": " + it.response.url + " - " + it.response.responseTime + "ms")
                        println(it.requestId + "<=" + it.responseBody)
                    }
                )
                spider.fetch("https://www.baidu.com", 1000L)
                println(1)
            } finally {
                spider.quit()
            }
        }
    }
}