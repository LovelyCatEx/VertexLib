package com.lovelycatv.vertex.spider.adatper.selenium.interceptor

import com.lovelycatv.vertex.spider.adatper.selenium.RemoteResponse

fun interface ResponseInterceptor : SeleniumInterceptor {
    fun intercept(response: RemoteResponse)
}