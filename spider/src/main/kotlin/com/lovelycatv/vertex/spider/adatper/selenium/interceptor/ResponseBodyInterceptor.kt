package com.lovelycatv.vertex.spider.adatper.selenium.interceptor

import com.lovelycatv.vertex.spider.adatper.selenium.RemoteResponseBody

fun interface ResponseBodyInterceptor : SeleniumInterceptor {
    fun intercept(response: RemoteResponseBody)
}