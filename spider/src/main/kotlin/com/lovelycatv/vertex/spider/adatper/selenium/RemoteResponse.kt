package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.HTTPStatus

data class RemoteResponse(
    val timestamp: Long,
    val url: String,
    val status: HTTPStatus,
)