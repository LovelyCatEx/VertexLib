package com.lovelycatv.vertex.ai.volc.asr.protocol

import com.lovelycatv.vertex.ai.network.VertexWebSocket
import com.lovelycatv.vertex.log.logger
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

/**
 * Low level WebSocket transport for the big model streaming ASR (SAUC) service.
 * Handles binary framing via [ASRMessage] and forwards parsed frames to [callback].
 *
 * @author lovelycat
 * @since 2026-08-01 00:00
 * @version 1.0
 */
class VolcanoASRWebSocketClient(
    private val apiUrl: String,
    private val client: OkHttpClient,
    val callback: StreamCallback,
    private val enableLogging: Boolean = false
) : VertexWebSocket(apiUrl, client) {

    private val logger = logger()

    init {
        initializeConnection()
    }

    fun initializeConnection() {
        if (enableLogging) {
            logger.info("Connecting to ASR WebSocket: $apiUrl")
        }

        super.connect()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        if (enableLogging) {
            logger.info("WebSocket connected. Logid=${response.header("X-Tt-Logid")}")
        }

        callback.onConnected()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        if (enableLogging) {
            logger.warn("Unexpected text message: $text")
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        try {
            val msg = ASRMessage.unmarshal(bytes.toByteArray())

            if (enableLogging) {
                logger.info("Received ASRMessage: $msg")
            }

            callback.onMessage(msg)
        } catch (e: Exception) {
            logger.error("Failed to parse ASR message", e)
            callback.onError(e)
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        if (enableLogging) {
            logger.info("WebSocket closing: $code, $reason")
        }

        callback.onClosing(code, reason)
        webSocket.close(code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        if (enableLogging) {
            logger.info("WebSocket closed: $code, $reason")
        }

        callback.onClosed(code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        // Surface the server's diagnostics on a rejected handshake (e.g. 400/401/403).
        // Volcano reports the real reason via X-Api-* headers and the response body.
        if (response != null) {
            val apiMessage = response.header("X-Api-Message")
            val apiStatus = response.header("X-Api-Status-Code")
            val logId = response.header("X-Tt-Logid")
            val body = try {
                response.body?.string()
            } catch (e: Exception) {
                null
            }
            logger.error(
                "WebSocket handshake failed: http=${response.code} ${response.message}, " +
                        "X-Api-Status-Code=$apiStatus, X-Api-Message=$apiMessage, " +
                        "X-Tt-Logid=$logId, body=$body",
                t
            )
        } else {
            logger.error("WebSocket failure", t)
        }

        callback.onError(t)
    }

    /**
     * Send the full client request that opens the recognition session.
     */
    fun sendFullClientRequest(payload: ByteArray, sequence: Int) {
        sendMessage(ASRMessage.fullClientRequest(payload, sequence))
    }

    /**
     * Send a single PCM audio segment.
     */
    fun sendAudioSegment(payload: ByteArray, sequence: Int, isLast: Boolean) {
        sendMessage(ASRMessage.audioOnlyRequest(payload, sequence, isLast))
    }

    private fun sendMessage(message: ASRMessage) {
        if (enableLogging) {
            logger.info("Sending ASRMessage: $message")
        }

        val sent = super.send(ByteString.of(*message.marshal()))

        if (sent) {
            callback.onSendSuccessfully(message)
        } else {
            callback.onSendFailed(message)
        }
    }

    interface StreamCallback {
        /** WebSocket connected */
        fun onConnected()

        /** Any frame received (already parsed as [ASRMessage]) */
        fun onMessage(message: ASRMessage)

        /** Any frame successfully sent */
        fun onSendSuccessfully(message: ASRMessage)

        /** Any frame send failed */
        fun onSendFailed(message: ASRMessage)

        /** Connection closing */
        fun onClosing(code: Int, reason: String)

        /** Connection closed */
        fun onClosed(code: Int, reason: String)

        /** Any error happened */
        fun onError(t: Throwable)
    }
}
