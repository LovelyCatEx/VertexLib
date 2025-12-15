package com.lovelycatv.vertex.ai.volc.tts.v3

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lovelycatv.vertex.ai.network.VertexOkHttp
import com.lovelycatv.vertex.ai.volc.tts.TTSException
import com.lovelycatv.vertex.ai.volc.tts.v3.protocol.EventType
import com.lovelycatv.vertex.ai.volc.tts.v3.protocol.MsgType
import com.lovelycatv.vertex.ai.volc.tts.v3.protocol.VolcanoTTSWebSocketClientV3
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author lovelycat
 * @since 2025-12-16 00:00
 * @version 1.0
 */
class VolcanoTTSClientV3(
    private val apiResourceId: String,
    private val config: VolcanoTTSClientV3Config,
    private val gson: Gson = Gson()
) {
    private val vertexOkHttp = VertexOkHttp(
        timeoutSeconds = this.config.timeoutSeconds,
        enableLogging = this.config.enableLogging,
        preInterceptor = { chain ->
            val originalRequest = chain.request()
            val requestWithAuth = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer; ${this.config.accessToken}")
                .addHeader("X-Api-App-Id", this.config.appId)
                .addHeader("X-Api-Access-Key", this.config.accessToken)
                .addHeader("X-Api-Request-Id", UUID.randomUUID().toString())
                .addHeader("X-Api-Resource-Id", this.apiResourceId)
                .addHeader("X-Control-Require-Usage-Tokens-Return", "*")
                .build()
            chain.proceed(requestWithAuth)
        }
    ).okHttpClient

    suspend fun sendHttpRequest(request: VolcanoTTSRequestV3) {
        this.sendHttpRequest(this.apiResourceId, request)
    }

    suspend fun sendHttpRequest(
        apiResourceId: String,
        request: VolcanoTTSRequestV3
    ): VolcanoTTSResponseV3 {
        val response = suspendCoroutine {
            val reqBody = this.gson.toJson(request)
            val body = reqBody.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request: Request = Request.Builder()
                .url(this.config.httpUrl)
                .addHeader("X-Api-Resource-Id", apiResourceId)
                .post(body)
                .build()

            it.resume(this.vertexOkHttp.newCall(request).execute())
        }

        val stringBody = response.body?.string() ?: throw TTSException(
            -1,
            "request has been sent successfully but response body is null"
        )

        val streamResponses = stringBody
            .split("\n")
            .filter { it.isNotBlank() }
            .map {
                this.gson.fromJson(it, VolcanoTTSResponseV3::class.java)
            }

        return VolcanoTTSResponseV3(
            code = streamResponses.find { it.code != 0 }?.code
                ?: throw TTSException(-1, "request has been sent successfully but response code is null"),
            message = streamResponses.find { it.message.isNotBlank() }?.message ?: "",
            data = streamResponses.joinToString(separator = "", prefix = "", postfix = "") { it.data ?: "" },
            sentence = streamResponses.find { it.sentence != null }?.sentence,
            usage = streamResponses.find { it.usage != null }?.usage
        )
    }

    fun sendWebSocketRequest(
        request: VolcanoTTSRequestV3,
        callback: VolcanoTTSWebSocketClientV3.StreamCallbackV3
    ) {
        this.sendWebSocketRequest(listOf(request), callback)
    }

    fun sendWebSocketRequest(
        requests: List<VolcanoTTSRequestV3>,
        callback: VolcanoTTSWebSocketClientV3.StreamCallbackV3
    ) {
        val client = VolcanoTTSWebSocketClientV3(
            apiUrl = this.config.webSocketUrl,
            client = this.vertexOkHttp,
            callback = callback,
            enableLogging = this.config.enableLogging
        )

        // Start connection
        client.sendStartConnection()
        // Wait for connection started
        client.waitForMessage(MsgType.FULL_SERVER_RESPONSE, EventType.CONNECTION_STARTED)

        requests.forEach { request ->
            val sessionId = UUID.randomUUID().toString()
            this.sendSingleWebSocketRequest(client, sessionId, request)
        }

        // End connection
        client.sendFinishConnection()
    }

    private fun sendSingleWebSocketRequest(
        client: VolcanoTTSWebSocketClientV3,
        sessionId: String,
        request: VolcanoTTSRequestV3
    ) {
        // Start session
        val startReq = mapOf(
            "user" to request.user,
            "namespace" to request.namespace,
            "req_params" to request.requestConfig,
            "event" to EventType.START_SESSION.value
        )

        client.sendStartSession(this.gson.toJson(startReq), sessionId)

        // Wait for session started
        client.waitForMessage(MsgType.FULL_SERVER_RESPONSE, EventType.SESSION_STARTED)

        // Send text
        client.sendTaskRequest(this.gson.toJson(request), sessionId)

        // End session
        client.sendFinishSession(sessionId)

        var sentence: VolcanoTTSResponseV3.Sentence? = null
        var usage: VolcanoTTSResponseV3.Usage? = null

        while (true) {
            val msg = client.receiveMessage()
            when (msg.type) {
                MsgType.FULL_SERVER_RESPONSE -> {
                    when (msg.event) {
                        EventType.TTS_SENTENCE_END -> {
                            sentence = this.gson.fromJson(
                                String(msg.payload),
                                VolcanoTTSResponseV3.Sentence::class.java
                            )
                        }

                        EventType.SESSION_FINISHED -> {
                            usage = this.gson.fromJson(
                                this.gson.fromJson(
                                    String(msg.payload),
                                    JsonObject::class.java
                                ).getAsJsonObject("usage").toString(),
                                VolcanoTTSResponseV3.Usage::class.java
                            )
                        }

                        else -> {}
                    }
                }

                MsgType.AUDIO_ONLY_SERVER -> {
                    client.callback.onReceived(msg.payload)
                }

                else -> throw RuntimeException("Unexpected message: $msg")
            }

            if (msg.event == EventType.SESSION_FINISHED) {
                checkNotNull(sentence) { "Received session finished without sentence information" }
                checkNotNull(usage) { "Received session finished without usage information" }

                client.callback.onCompleted(sentence, usage)
                break
            }
        }
    }


    /**
     * Get resource ID based on voice type
     *
     * @param voice Voice type string
     * @return Corresponding resource ID
     */
    fun voiceToResourceId(voice: String): String {
        // Map different voice types to resource IDs based on actual needs
        if (voice.startsWith("S_")) {
            return "volc.megatts.default"
        }
        return "volc.service_type.10029"
    }
}