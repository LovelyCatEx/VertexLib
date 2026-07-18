package com.lovelycatv.vertex.spider.app.bilibili.space

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONWriter
import com.lovelycatv.vertex.spider.RequestOptions
import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupSpider
import com.lovelycatv.vertex.spider.adatper.jsoup.JsoupSpiderOptions
import com.lovelycatv.vertex.spider.app.bilibili.misc.BilibiliWbiParser
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BilibiliSpaceParserTest {
    private val cookie = System.getProperty("bilibili.cookie") ?: "buvid3=99A32CC9-4FBA-5865-248D-0162A7F898E444448infoc; b_nut=1769938144; _uuid=F82109106E-63DB-213E-BF75-57110DAC77E6404925infoc; buvid_fp=88f738f9852b14b0cfaafc51cf966d4f; rpdid=|(mmJR~~lm~0J'u~YmmYkRum; SESSDATA=6bde9bd9%2C1785514455%2C7c306%2A22CjDL-zDbl_8PNpm_pUVgKejMaX1pTyGuFGRDe7UZJUdFseAUsDFIWa0jeEL9JS1n4ZgSVkNTQnh3eThkNlhWcDdNaWI4T25uVUVqUkFtSFdzVUI1TnZjWE85LVIzT09RZFJKZDZJYUQ5NXVwQ2ZCQzdCU05pclNqek41ODFaNV9CLTEzSlVSYTBBIIEC; bili_jct=6379049777acf28854f62175bff48e03; DedeUserID=94898339; DedeUserID__ckMd5=2d0ca98b875697a7; theme-tip-show=SHOWED; theme_style=light; theme-avatar-tip-show=SHOWED; LIVE_BUVID=AUTO2817718735970725; PVID=1; buvid4=89FEBAB7-39D8-33F7-AE49-C955DF099ACG39336-026070416-9NBj6x/THJ47fZPI2eHiVA%3D%3D; home_feed_column=5; browser_resolution=1800-925; sid=7ilxxsa6; CURRENT_QUALITY=64; bili_ticket=eyJhbGciOiJIUzI1NiIsImtpZCI6InMwMyIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3ODQ0NzM4OTcsImlhdCI6MTc4NDIxNDYzNywicGx0IjotMX0.2cDboQAfgKjeje4jdJXHq6ZQ96KWjZnw_LNgLEL6Lfw; bili_ticket_expires=1784473837; CURRENT_FNVAL=2000; b_lsid=CDBD41E2_19F7603EFC9"

    private val spider = JsoupSpider(
        JsoupSpiderOptions(
            requestOptions = RequestOptions(
                headers = mapOf(
                    "Accept-Encoding" to listOf("gzip, deflate, br, zstd"),
                    "Accept-Language" to listOf("zh-CN,zh;q=0.9"),
                    "Cookie" to listOf(cookie)
                ),
                referer = "https://www.bilibili.com"
            )
        )
    )

    private val wbiParser = BilibiliWbiParser(spider)
    private val spaceParser = BilibiliSpaceParser(spider)

    private val mid = 546195L
    private val spaceUrl = "https://space.bilibili.com/$mid"

    @Test
    fun getAllPublishedVideos() {
        runBlocking {
            val wbi = wbiParser.getWbiImage(cookie)
            val result = spaceParser.getAllPublishedVideos(mid, wbi, 500L)
            println(JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat))
            assertEquals(result.page.total, result.videos.size)
        }
    }

    @Test
    fun getPublishedVideos() {
        runBlocking {
            val wbi = wbiParser.getWbiImage(cookie)
            val result = spaceParser.getPublishedVideos(mid, wbi, "", 1)
            println(JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat))
        }
    }

    @Test
    fun extractMidFromSpaceUrl() {
        assertEquals(mid, BilibiliSpaceParser.extractMidFromSpaceUrl(spaceUrl))
    }
}