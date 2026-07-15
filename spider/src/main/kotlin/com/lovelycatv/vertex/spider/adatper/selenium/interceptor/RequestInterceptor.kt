package com.lovelycatv.vertex.spider.adatper.selenium.interceptor

import com.lovelycatv.vertex.spider.adatper.selenium.RemoteRequest

fun interface RequestInterceptor : SeleniumInterceptor {
    fun intercept(response: RemoteRequest)
}