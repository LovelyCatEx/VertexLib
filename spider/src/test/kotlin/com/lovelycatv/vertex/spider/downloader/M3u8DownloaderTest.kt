package com.lovelycatv.vertex.spider.downloader

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URL

class M3u8DownloaderTest {
    @Test
    fun download() {
        val downloader = M3u8Downloader(
            DownloadConfig(),
            File(File("").canonicalPath, "downloads/tmp")
        )

        val m3u8Url = ""
        if (m3u8Url.isEmpty()) {
            return
        }
        val baseUrl = m3u8Url.substringBeforeLast("/") + "/"

        runBlocking {
            downloader.download(
                URL(m3u8Url).readText(),
                baseUrl,
                outputDir = File(File("").canonicalPath, "/downloads").canonicalPath,
                fileName = "test"
            ).await()
        }
    }

}