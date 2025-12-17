package com.lovelycatv.vertex.ai.volc.tts.v1

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.network.VertexOkHttp
import com.lovelycatv.vertex.ai.volc.VolcanoEngineConstants
import com.lovelycatv.vertex.ai.volc.tts.TTSException
import com.lovelycatv.vertex.ai.volc.tts.v1.protocol.VolcanoTTSWebsocketClientV1
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * @author lovelycat
 * @since 2025-12-15 16:29
 * @version 1.0
 */
class VolcanoTTSClientV1(
    private val apiKey: String,
    timeoutSeconds: Long = 60,
    private val enableLogging: Boolean = false,
    private val gson: Gson = Gson(),
    private val httpUrl: String = VolcanoEngineConstants.TTS_V1_HTTP_API_URL,
    private val webSocketUrl: String = VolcanoEngineConstants.TTS_V1_WS_API_URL
) {
    private val vertexOkHttp = VertexOkHttp(
        timeoutSeconds = timeoutSeconds,
        enableLogging = enableLogging,
        preInterceptor = { chain ->
            val originalRequest = chain.request()
            val requestWithAuth = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer; $apiKey")
                .build()
            chain.proceed(requestWithAuth)
        }
    ).okHttpClient

    suspend fun sendHttpRequest(request: VolcanoTTSRequestV1): VolcanoTTSResponseV1 {
        val response = suspendCoroutine {
            val reqBody = gson.toJson(request)
            val body = reqBody.toRequestBody("application/json; charset=utf-8".toMediaType())
            val req: Request = Request.Builder()
                .url(httpUrl)
                .post(body)
                .build()

            it.resume(this.vertexOkHttp.newCall(req).execute())
        }

        val stringBody = response.body?.string() ?: throw TTSException(
            -1,
            "request has been sent successfully but response body is null"
        )

        return gson.fromJson(stringBody, VolcanoTTSResponseV1::class.java)
    }

    fun sendWebSocketRequest(request: VolcanoTTSRequestV1, callback: VolcanoTTSWebsocketClientV1.StreamCallbackV1) {
        val ttsWebsocketClient = VolcanoTTSWebsocketClientV1(
            this.webSocketUrl,
            this.vertexOkHttp,
            this.gson,
            callback,
            enableLogging
        )

        ttsWebsocketClient.submit(request)
    }
}