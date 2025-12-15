package com.lovelycatv.vertex.ai.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author lovelycat
 * @since 2025-12-15 17:29
 * @version 1.0
 */
class VertexRetrofit(
    baseUrl: String,
    timeoutSeconds: Long = 60,
    enableLogging: Boolean = false,
    converterFactory: Converter.Factory = GsonConverterFactory.create(),
    preInterceptor: (Interceptor.Chain) -> Response = { it.proceed(it.request()) },
    interceptors: List<Interceptor> = listOf()
) {
    val okHttpClient: OkHttpClient = VertexOkHttp(
        timeoutSeconds,
        enableLogging,
        preInterceptor,
        interceptors
    ).okHttpClient

    val retrofit: Retrofit

    init {
        val retrofitBuilder = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(converterFactory)

        //  Apply baseUrl
        retrofitBuilder.baseUrl(baseUrl)

        retrofit = retrofitBuilder.build()
    }
}