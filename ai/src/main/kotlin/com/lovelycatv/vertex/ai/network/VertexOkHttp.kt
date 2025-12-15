package com.lovelycatv.vertex.ai.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * @author lovelycat
 * @since 2025-12-15 19:22
 * @version 1.0
 */
class VertexOkHttp(
    timeoutSeconds: Long = 60,
    enableLogging: Boolean = false,
    preInterceptor: (Interceptor.Chain) -> Response = { it.proceed(it.request()) },
    interceptors: List<Interceptor> = listOf()
) {
    val okHttpClient: OkHttpClient

    init {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)

        // Apply preInterceptor
        clientBuilder.addInterceptor { chain -> preInterceptor.invoke(chain) }

        interceptors.forEach {
            clientBuilder.addInterceptor(it)
        }

        if (enableLogging) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            clientBuilder.addInterceptor(loggingInterceptor)
        }

        okHttpClient = clientBuilder.build()
    }
}