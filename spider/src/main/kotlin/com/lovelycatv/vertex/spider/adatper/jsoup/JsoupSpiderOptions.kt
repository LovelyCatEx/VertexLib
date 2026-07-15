package com.lovelycatv.vertex.spider.adatper.jsoup

import com.lovelycatv.vertex.spider.RequestOptions
import com.lovelycatv.vertex.spider.UserAgent
import com.lovelycatv.vertex.spider.VertexSpiderOptions

class JsoupSpiderOptions(
    userAgent: String = UserAgent.chromeOnWindows().toString(),
    connectionTimeout: Long = 10000L,
    val requestOptions: RequestOptions = RequestOptions(),
) : VertexSpiderOptions(userAgent, connectionTimeout)