package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.spider.HTTPStatus

data class RemoteResponse(
    val requestId: String,
    val request: RemoteRequest,
    val timestamp: Long,
    val url: String,
    val status: HTTPStatus,
    val headers: Map<String, Any?>,
) {
    val responseTime = this.timestamp - this.request.timestamp
}