package com.lovelycatv.vertex.spider.adatper.selenium

fun interface ResponseInterceptor : SeleniumInterceptor {
    fun intercept(response: RemoteResponse)
}