package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.HTTPMethod

data class RemoteRequest(
    val requestId: String,
    val method: HTTPMethod,
    val url: String,
    val timestamp: Long,
    val headers: Map<String, Any?>,
)
