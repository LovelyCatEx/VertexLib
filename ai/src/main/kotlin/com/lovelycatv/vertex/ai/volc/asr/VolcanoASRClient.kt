package com.lovelycatv.vertex.ai.volc.asr

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.network.VertexOkHttp
import com.lovelycatv.vertex.ai.volc.asr.protocol.ASRMessage
import com.lovelycatv.vertex.ai.volc.asr.protocol.MsgType
import com.lovelycatv.vertex.ai.volc.asr.protocol.VolcanoASRWebSocketClient
import java.util.UUID

/**
 * High level client for the big model streaming ASR (SAUC) service.
 *
 * Usage: call [startStream] to open a recognition session, push captured PCM
 * segments through [VolcanoASRStream.sendAudio], and observe results via the
 * supplied [VolcanoASRStreamCallback].
 *
 * Document: https://docs.volcengine.com/docs/6561/1354869
 *
 * @author lovelycat
 * @since 2026-08-01 00:00
 * @version 1.0
 */
class VolcanoASRClient(
    private val config: VolcanoASRClientConfig,
    private val gson: Gson = Gson()
) {
    private val vertexOkHttp = VertexOkHttp(
        timeoutSeconds = this.config.timeoutSeconds,
        enableLogging = this.config.enableLogging,
        preInterceptor = { chain ->
            val originalRequest = chain.request()
            val requestWithAuth = originalRequest.newBuilder()
                .addHeader("X-Api-App-Key", this.config.appId)
                .addHeader("X-Api-Access-Key", this.config.accessToken)
                .addHeader("X-Api-Resource-Id", this.config.resourceId)
                .addHeader("X-Api-Connect-Id", UUID.randomUUID().toString())
                .build()
            chain.proceed(requestWithAuth)
        }
    ).okHttpClient

    /**
     * Open a recognition session. The returned [VolcanoASRStream] is used to push
     * audio segments; recognition results are delivered through [callback].
     *
     * @param request the session configuration (user id, audio format, options)
     * @param callback receives lifecycle and recognition events
     */
    fun startStream(
        request: VolcanoASRRequest,
        callback: VolcanoASRStreamCallback
    ): VolcanoASRStream {
        return VolcanoASRStream(
            config = this.config,
            client = this.vertexOkHttp,
            request = request,
            callback = callback,
            gson = this.gson
        )
    }

    /**
     * A single recognition session. Not thread-safe: [sendAudio] should be called
     * from a single producer (e.g. the microphone read loop).
     */
    class VolcanoASRStream(
        private val config: VolcanoASRClientConfig,
        client: okhttp3.OkHttpClient,
        request: VolcanoASRRequest,
        private val callback: VolcanoASRStreamCallback,
        private val gson: Gson
    ) {
        private var sequence = 1
        private var finished = false

        private val wsClient = VolcanoASRWebSocketClient(
            apiUrl = this.config.webSocketUrl,
            client = client,
            enableLogging = this.config.enableLogging,
            callback = object : VolcanoASRWebSocketClient.StreamCallback {
                override fun onConnected() {}

                override fun onMessage(message: ASRMessage) {
                    when (message.type) {
                        MsgType.SERVER_FULL_RESPONSE, MsgType.SERVER_ACK -> {
                            val response = if (message.payload.isNotEmpty()) {
                                gson.fromJson(String(message.payload), VolcanoASRResponse::class.java)
                            } else {
                                VolcanoASRResponse()
                            }

                            callback.onResult(response, message.isLastPackage)
                            if (message.isLastPackage) {
                                callback.onCompleted(response)
                            }
                        }

                        MsgType.SERVER_ERROR_RESPONSE -> {
                            callback.onError(
                                ASRException(message.errorCode, String(message.payload))
                            )
                        }

                        else -> {}
                    }
                }

                override fun onSendSuccessfully(message: ASRMessage) {}

                override fun onSendFailed(message: ASRMessage) {
                    callback.onError(ASRException(-1, "Failed to send frame: $message"))
                }

                override fun onClosing(code: Int, reason: String) {}

                override fun onClosed(code: Int, reason: String) {}

                override fun onError(t: Throwable) {
                    callback.onError(t)
                }
            }
        )

        init {
            // Open the session with the full client request (sequence = 1)
            wsClient.sendFullClientRequest(gson.toJson(request).toByteArray(), sequence)
            callback.onReady()
        }

        /**
         * Push a single PCM audio segment to the recognizer.
         *
         * @param audio raw PCM bytes matching the request's audio format
         * @param isLast whether this is the final segment; sending it closes the
         *               input stream and the server will return the last package
         */
        fun sendAudio(audio: ByteArray, isLast: Boolean) {
            check(!finished) { "Stream already finished" }

            sequence += 1
            wsClient.sendAudioSegment(audio, sequence, isLast)

            if (isLast) {
                finished = true
            }
        }

        /**
         * Signal end of input by sending an empty terminating segment. Prefer
         * passing isLast=true to the final [sendAudio] call when possible.
         */
        fun finish() {
            if (!finished) {
                sendAudio(ByteArray(0), isLast = true)
            }
        }

        /**
         * Close the underlying WebSocket connection.
         */
        fun close() {
            wsClient.close("client closed")
        }
    }
}
