package com.lovelycatv.vertex.spider.downloader

import java.io.File

sealed class DownloadResult {
    data class Success(val file: File, val size: Long) : DownloadResult()
    
    data class Failure(val code: Int?, val message: String) : DownloadResult()
    
    fun isSuccess(): Boolean = this is Success
}