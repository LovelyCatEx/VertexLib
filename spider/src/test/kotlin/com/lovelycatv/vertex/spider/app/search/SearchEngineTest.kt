package com.lovelycatv.vertex.spider.app.search

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONWriter
import com.lovelycatv.vertex.spider.RequestOptions
import com.lovelycatv.vertex.spider.UserAgent
import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupSpider
import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupSpiderOptions
import com.lovelycatv.vertex.spider.app.search.baidu.BaiduSearch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class SearchEngineTest {
    private val baiduSpider = JsoupSpider(
        JsoupSpiderOptions(
            userAgent = UserAgent.chromeOnWindows().toString(),
            requestOptions = RequestOptions(
                referer = "https://www.baidu.com"
            )
        )
    )

    private val baiduSearch = BaiduSearch(baiduSpider)
    private val searchEngines = listOf(baiduSearch)

    @Test
    fun searchRecursively() {
        searchEngines.forEach { engine ->
            runBlocking {
                val result = engine.searchRecursively("Hello, World!", 10)
                println(JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat))
            }
        }
    }

    @Test
    fun search() {
        searchEngines.forEach { engine ->
            runBlocking {
                val result = engine.search("Hello, World!", 1)
                println(JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat))
            }
        }
    }
}