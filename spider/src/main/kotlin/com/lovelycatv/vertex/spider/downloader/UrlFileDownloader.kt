package com.lovelycatv.vertex.spider.downloader

import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.SocketTimeoutException
import kotlin.time.Duration.Companion.milliseconds

class UrlFileDownloader(
    private val config: DownloadConfig,
) {
    private val logger = logger()

    suspend fun download(url: String, outputFile: File): DownloadResult =
        withContext(Dispatchers.IO) {
            downloadWithRetry(url, outputFile,0)
        }

    suspend fun downloadFromCandidateUrls(urls: List<String>, outputFile: File): DownloadResult =
        withContext(Dispatchers.IO) {
            if (urls.isEmpty()) {
                return@withContext DownloadResult.Failure(null, "Empty url list")
            }

            val size = urls.size

            for ((index, url) in urls.withIndex()) {
                logger.info("[${index + 1}/$size] Trying to download url $url")
                val result = downloadWithRetry(url, outputFile, 0)
                if (result is DownloadResult.Success) {
                    return@withContext result
                } else {
                    logger.info("[${index + 1}/$size] Failed to download url $url")
                }
            }

            DownloadResult.Failure(null, "All url were failed to be downloaded")
        }

    private suspend fun downloadWithRetry(
        url: String,
        outputFile: File,
        retryCount: Int
    ): DownloadResult {
        if (retryCount >= config.maxRetries) {
            return DownloadResult.Failure(null, "Reached max retries")
        }

        if (retryCount > 0) {
            delay(100L.milliseconds)
        }

        return try {
            doDownload(url, outputFile)
        } catch (_: SocketTimeoutException) {
            downloadWithRetry(url, outputFile, retryCount + 1)
        } catch (e: IOException) {
            if (e.message?.contains("timeout") == true) {
                downloadWithRetry(url, outputFile, retryCount + 1)
            } else {
                DownloadResult.Failure(null, "IOException: ${e.message}")
            }
        } catch (e: Exception) {
            DownloadResult.Failure(null, "Unknown error: ${e.message}")
        }
    }

    private fun doDownload(url: String, outputFile: File): DownloadResult {
        val parentDir = outputFile.parentFile
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs()
        }

        var response: Connection.Response? = null
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        return try {
            val connection = Jsoup.connect(url)
                .userAgent(config.userAgent)
                .timeout(config.timeoutMillis)
                .ignoreContentType(true)
                .followRedirects(config.followRedirects)
                .method(Connection.Method.GET)
                .headers(config.headers)

            config.referer?.let {
                connection.referrer(it)
            }


            connection.header("Accept", "*/*")
            connection.header("Accept-Encoding", "identity")
            connection.header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
            connection.header("Cache-Control", "no-cache")

            response = connection.execute()
            when (val statusCode = response.statusCode()) {
                200 -> {
                    val length = (response.header("Content-Length") ?: "0").toLong()
                    inputStream = response.bodyStream()
                    outputStream = FileOutputStream(outputFile)

                    val buffer = ByteArray(config.bufferSize)
                    var bytesRead: Int
                    var totalBytes = 0L
                    var lastPercentage = 0

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytes += bytesRead

                        val percentage = ((totalBytes.toDouble() / length) * 100).toInt()
                        if (percentage > lastPercentage) {
                            logger.info("Downloading: ${formatSize(totalBytes)}/${formatSize(length)} [${"*".repeat(percentage)}${" ".repeat(100 - percentage)}] $percentage%")
                        }

                        lastPercentage = percentage
                    }

                    logger.info("Download successfully: ${outputFile.absolutePath} (size: ${formatSize(totalBytes)})")
                    return DownloadResult.Success(outputFile, totalBytes)
                }

                301, 302, 303, 307, 308 -> {
                    val location = response.header("Location")
                    return if (!location.isNullOrBlank()) {
                        logger.info("Redirect to: $location")
                        doDownload(location, outputFile)
                    } else {
                        DownloadResult.Failure(statusCode, "Could not found redirect location")
                    }
                }

                403 -> DownloadResult.Failure(statusCode, "HTTP 403 Forbidden")
                404 -> DownloadResult.Failure(statusCode, "HTTP 404 Not Found")
                416 -> DownloadResult.Failure(statusCode, "HTTP 416 Range Not Satisfiable")
                else -> {
                    val errorMsg = when (statusCode) {
                        in 400..499 -> "499"
                        in 500..599 -> "599"
                        else -> "Unknown status code"
                    }
                    DownloadResult.Failure(statusCode, "HTTP $statusCode - $errorMsg")
                }
            }

        } catch (e: HttpStatusException) {
            DownloadResult.Failure(e.statusCode, "Status: ${e.message}")
        } catch (e: SocketTimeoutException) {
            throw e
        } catch (e: java.net.UnknownHostException) {
            DownloadResult.Failure(null, "Unknown host: ${e.message}")
        } catch (e: java.io.FileNotFoundException) {
            DownloadResult.Failure(null, "File not found: ${e.message}")
        } catch (e: Exception) {
            DownloadResult.Failure(null, "Unknown error: ${e.message}")
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: Exception) {
            }
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