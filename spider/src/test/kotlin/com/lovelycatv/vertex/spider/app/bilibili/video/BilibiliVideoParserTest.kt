package com.lovelycatv.vertex.spider.app.bilibili.video

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONWriter
import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupSpider
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BilibiliVideoParserTest {
    private val parser = BilibiliVideoParser(
        jsoupSpider = JsoupSpider(),
    )

    private val url = "https://www.bilibili.com/video/BV1DF411e7rc"

    @Test
    fun getPlayerInfo() {
        runBlocking {
            println("parsing video...")
            val video = parser.parseVideo(url)
            println(JSON.toJSONString(video, JSONWriter.Feature.PrettyFormat))

            println("fetching video player info...")
            val result = parser.getPlayerInfo(video)
            println(JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat))
        }
    }

    @Test
    fun parseVideo() {
        runBlocking {
            val video = parser.parseVideo(url)
            println(JSON.toJSONString(video, JSONWriter.Feature.PrettyFormat))
        }
    }

    @Test
    fun extractBVIdFromUrl() {
        val expected = "BV1DF411e7rc"

        val url1 = "https://www.bilibili.com/video/BV1DF411e7rc"
        val result1 = BilibiliVideoParser.extractBVIdFromUrl(url1)

        val url2 = "https://www.bilibili.com/video/BV1DF411e7rc/"
        val result2 = BilibiliVideoParser.extractBVIdFromUrl(url2)

        val url3 = "https://www.bilibili.com/video/BV1DF411e7rc/?spm_id_from=333.337.search-card.all.click"
        val result3 = BilibiliVideoParser.extractBVIdFromUrl(url3)

        assertEquals(expected, result1)
        assertEquals(expected, result2)
        assertEquals(expected, result3)
    }

}