package com.lovelycatv.vertex.spider.downloader

import com.lovelycatv.vertex.spider.UserAgent

data class DownloadConfig(
    val userAgent: String = UserAgent.chromeOnWindows().toString(),
    val referer: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val timeoutMillis: Int = 30000,
    val maxRetries: Int = 3,
    val retryDelayMillis: Long = 1000,
    val followRedirects: Boolean = true,
    val bufferSize: Int = 8192,
)