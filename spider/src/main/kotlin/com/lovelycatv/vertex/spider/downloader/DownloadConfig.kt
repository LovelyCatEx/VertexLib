package com.lovelycatv.vertex.spider.downloader

data class DownloadConfig(
    val userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    val referer: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val timeoutMillis: Int = 30000,
    val maxRetries: Int = 3,
    val retryDelayMillis: Long = 1000,
    val followRedirects: Boolean = true,
    val bufferSize: Int = 8192,
)