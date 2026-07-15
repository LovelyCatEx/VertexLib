package com.lovelycatv.vertex.spider.downloader

import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class M3u8Downloader(
    private val config: DownloadConfig = DownloadConfig(),
    private val tempDir: File,
    maxParallelism: Int = 10,
) {
    private val urlFileDownloader = UrlFileDownloader(
        config = config,
        log = false,
    )

    private val logger = logger()

    private val semaphore = Semaphore(maxParallelism)

    private val coroutineScope = CoroutineScope(
        Dispatchers.IO + CoroutineName("M3u8Downloader")
    )

    fun download(
        url: String,
        outputDir: String,
        fileName: String
    ): Deferred<File> {
        logger.info("Preparing to download m3u8: $url, outputDir=$outputDir, fileName=$fileName")
        return this.download(
            url.let {
                val connection = Jsoup.connect(it)
                    .userAgent(config.userAgent)
                    .timeout(config.timeoutMillis)
                    .ignoreContentType(true)
                    .followRedirects(config.followRedirects)
                    .method(Connection.Method.GET)
                    .headers(config.headers)

                config.referer?.let {
                    connection.referrer(it)
                }

                connection.execute().body()
            },
            url.substringBeforeLast("/") + "/",
            outputDir,
            fileName
        )
    }

    fun download(
        m3u8Content: String,
        baseUrl: String,
        outputDir: String,
        fileName: String
    ): Deferred<File> {
        logger.info("Preparing to download m3u8, contentLength=${m3u8Content.length}, outputDir=$outputDir, fileName=$fileName")
        return coroutineScope.async(CoroutineName("M3U8-$fileName")) {
            // 1. Parse m3u8
            val parsed = parseM3u8(m3u8Content, baseUrl)

            // 2. Download key
            val keyBytes = parsed.keyUrl?.let { downloadKey(it) }

            // 3. Create output directory
            val outputDirFile = File(outputDir)
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs()
            }

            // 4. Download all ts phases
            val tempDir = File(tempDir, fileName)
            tempDir.mkdirs()
            val tempFiles = mutableListOf<File>()
            val total = parsed.tsUrls.size

            val latch = CountDownLatch(total)
            var completed = 0
            var accLength = 0L

            parsed.tsUrls.forEachIndexed { index, tsUrl ->
                semaphore.withPermit {
                    try {
                        val tempFile = File(tempDir, "/temp_$index.ts")
                        if (!tempFile.exists() || tempFile.length() == 0L) {
                            downloadTs(tsUrl, tempFile, keyBytes, parsed.iv)
                        } else {
                            logger.info("ts part $index already exists, skipped.")
                        }

                        synchronized(tempFiles) {
                            tempFiles.add(tempFile)
                        }

                        completed++

                        val deltaLength = tempFile.length()
                        accLength += deltaLength

                        val percentage = ((completed.toDouble() / total) * 100).toInt()
                        logger.info("Downloading: $completed/$total " +
                                "(+${formatSize(deltaLength)}/${formatSize(accLength)}) " +
                                "[${"*".repeat(percentage)}${" ".repeat(100 - percentage)}] $percentage%"
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        latch.countDown()
                    }
                }
            }

            // Waiting...
            val success = latch.await(300, TimeUnit.SECONDS)
            if (!success) {
                throw IllegalStateException("Download m3u8 timeout")
            }

            // 5. Merge all ts files
            val outputFile = File(outputDir, "$fileName.ts")
            mergeTsFiles(tempFiles, outputFile)

            // 6. Clear tmp files
            tempFiles.forEach { it.delete() }

            logger.info("m3u8 download successfully, fileSize=${outputFile.length()}bytes, path: ${outputFile.canonicalPath}")

            outputFile
        }
    }

    fun parseM3u8(content: String, baseUrl: String): M3u8Info {
        val lines = content.lines()
        var keyUrl: String? = null
        var iv: ByteArray? = null
        var method: String? = null
        val tsUrls = mutableListOf<String>()

        lines.forEach { line ->
            val trimmed = line.trim()

            when {
                // Encryption
                trimmed.startsWith("#EXT-X-KEY:") -> {
                    val keyInfo = parseKeyInfo(trimmed.removePrefix("#EXT-X-KEY:"), baseUrl)
                    keyUrl = keyInfo.url
                    iv = keyInfo.iv
                    method = keyInfo.method
                }
                // ts phase
                trimmed.startsWith("http") && trimmed.contains(".ts") -> {
                    tsUrls.add(trimmed)
                }
                // Relative .ts
                !trimmed.startsWith("#") && trimmed.isNotEmpty() && trimmed.endsWith(".ts") -> {
                    tsUrls.add(resolveUrl(baseUrl, trimmed))
                }
                // Skip comments
                trimmed.startsWith("#") -> {
                    // Skip comments
                }
            }
        }

        return M3u8Info(
            tsUrls = tsUrls,
            keyUrl = keyUrl,
            iv = iv,
            keyMethod = method
        )
    }

    /**
     * Parse ext-key line to [M3U8KeyInfo]
     *
     * @param line eg: METHOD=AES-128,URI="",IV=0x0000
     * @param baseUrl Base url
     */
    fun parseKeyInfo(line: String, baseUrl: String): M3U8KeyInfo {
        val parts = line.split(",")
        var url: String? = null
        var iv: ByteArray? = null
        var method: String? = null

        parts.forEach { part ->
            when {
                part.startsWith("METHOD=") -> {
                    method = part.substringAfter("=").replace("\"", "")
                }
                part.startsWith("URI=") -> {
                    val uri = part.substringAfter("=").replace("\"", "")
                    url = if (uri.startsWith("http")) uri else resolveUrl(baseUrl, uri)
                }
                part.startsWith("IV=") -> {
                    val hex = part.substringAfter("=").replace("0x", "")
                    iv = hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                }
            }
        }

        return M3U8KeyInfo(url, iv, method)
    }

    private suspend fun downloadKey(keyUrl: String): ByteArray {
        val keyFile = File(tempDir, "key_${keyUrl.hashCode()}.bin")
        val result = urlFileDownloader.download(keyUrl, keyFile, retryable = false)
        if (result !is DownloadResult.Success) {
            throw IllegalStateException("Failed to download m3u8 key: $keyUrl (${(result as DownloadResult.Failure).message})")
        }

        return keyFile.readBytes().also {
            keyFile.delete()
            logger.info("M3U8 Key downloaded: $keyUrl")
        }
    }

    private suspend fun downloadTs(
        tsUrl: String,
        outputFile: File,
        keyBytes: ByteArray?,
        iv: ByteArray?
    ) {
        val result = urlFileDownloader.download(tsUrl, outputFile, retryable = false)
        if (result !is DownloadResult.Success) {
            throw IllegalStateException("Failed to download ts: $tsUrl (${(result as DownloadResult.Failure).message})")
        }

        if (keyBytes != null && iv != null) {
            val decrypted = decryptTs(outputFile.readBytes(), keyBytes, iv)
            outputFile.writeBytes(decrypted)
        }
    }

    private fun decryptTs(data: ByteArray, keyBytes: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(keyBytes, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        return cipher.doFinal(data)
    }

    private fun mergeTsFiles(files: List<File>, outputFile: File) {
        logger.info("Merging ${files.size} ts files...")
        val sortedFiles = files.sortedBy { it.name.split("_")[1].replace(".ts", "").toInt() }
        sortedFiles.forEachIndexed { index, it ->
            logger.debug("  - {}: {}", index, it.canonicalPath)
        }
        FileOutputStream(outputFile).use { fos ->
            sortedFiles.forEach { file ->
                if (file.exists()) {
                    file.inputStream().use { fis ->
                        fis.copyTo(fos)
                    }
                }
            }
        }
        logger.info("Merged ${sortedFiles.size} ts files to ${outputFile.canonicalPath}")
    }

    private fun resolveUrl(baseUrl: String, relative: String): String {
        return if (relative.startsWith("http")) {
            relative
        } else {
            val base = baseUrl.substringBeforeLast("/")
            "$base/$relative"
        }
    }

    private fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${String.format("%.2f", bytes / (1024.0 * 1024.0))} MB"
            else -> "${String.format("%.2f", bytes / (1024.0 * 1024.0 * 1024.0))} GB"
        }
    }
}