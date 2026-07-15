package com.lovelycatv.vertex.spider.adatper.selenium

data class RemoteResponseBody(
    val requestId: String,
    val response: RemoteResponse,
    val responseBody: String,
) {
    val request: RemoteRequest = this.response.request
}