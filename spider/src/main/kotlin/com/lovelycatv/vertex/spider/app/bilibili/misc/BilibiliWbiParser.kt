package com.lovelycatv.vertex.spider.app.bilibili.misc

import com.alibaba.fastjson2.JSONObject
import com.lovelycatv.vertex.spider.RequestOptions
import com.lovelycatv.vertex.spider.VertexSpider
import com.lovelycatv.vertex.spider.app.bilibili.misc.types.BilibiliWbi

class BilibiliWbiParser(
    private val spider: VertexSpider,
) {
    suspend fun getWbiImage(cookie: String): BilibiliWbi {
        val result = this.spider.get(
            "https://api.bilibili.com/x/web-interface/nav",
            RequestOptions(
                headers = mapOf(
                    "Cookie" to listOf(cookie),
                )
            )
        )

        val data = JSONObject.parseObject(result).getJSONObject("data")
        val wbi = data.getJSONObject("wbi_img")
        return BilibiliWbi(
            img = wbi.getString("img_url").getUrlFileName(),
            sub = wbi.getString("sub_url").getUrlFileName(),
        )
    }

    private fun String.getUrlFileName(): String {
        val fileName = this.split('/').last()
        return fileName.substringBeforeLast('.')
    }
}