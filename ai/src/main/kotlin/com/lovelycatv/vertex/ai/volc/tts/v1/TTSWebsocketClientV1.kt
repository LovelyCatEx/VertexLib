package com.lovelycatv.vertex.ai.volc.tts.v1

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.volc.WebSocketCloseCode
import com.lovelycatv.vertex.ai.volc.tts.TTSException
import com.lovelycatv.vertex.log.logger
import okhttp3.*
import okio.ByteString
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * @author lovelycat
 * @since 2025-12-15 18:10
 * @version 1.0
 */
class TTSWebsocketClientV1(
    private val apiUrl: String,
    private val client: OkHttpClient,
    private val gson: Gson,
    private val streamCallback: StreamCallback,
    private val enableLogging: Boolean = false
) : WebSocketListener() {

    private val logger = logger()

    // buffer for current request's audio data
    private val buffer = ByteArrayOutputStream()

    // long-lived websocket connection
    @Volatile
    private lateinit var webSocket: WebSocket

    init {
        this.initializeConnection()
    }

    fun initializeConnection() {
        if (enableLogging) {
            this.logger.info("Ready to connect to websocket: ${this.apiUrl}")
        }

        val request: Request = Request.Builder()
            .url(apiUrl)
            .build()

        // create WebSocket connection (async, but usually fast)
        this.webSocket = client.newWebSocket(request, this)
    }

    /**
     * Submit TTS request synchronously and return audio bytes.
     * Uses a long-lived WebSocket connection, does NOT close on success.
     */
    fun submit(ttsRequest: TTSRequestV1) {
        val json: String = gson.toJson(ttsRequest)
        val jsonBytes = json.toByteArray(StandardCharsets.UTF_8)
        val header = byteArrayOf(0x11, 0x10, 0x10, 0x00)
        val requestByte = ByteBuffer.allocate(8 + jsonBytes.size)
        requestByte.put(header).putInt(jsonBytes.size).put(jsonBytes)

        if (enableLogging) {
            logger.info("ready to submit ttsRequest, header: {}, message: {}", header, json)
        }

        val sent: Boolean = this.webSocket.send(ByteString.of(*requestByte.array()))

        if (sent) {
            this.streamCallback.onSendSuccessful()
        } else {

            this.streamCallback.onSendFailed()
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        if (enableLogging) {
            logger.info("connection established")
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        if (enableLogging) {
            logger.info("received text message: {}", text)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        val byteBuffer = bytes.asByteBuffer()

        val protocolVersion = (byteBuffer.get(0).toInt() and 0xff) shr 4
        val headerSize = byteBuffer.get(0).toInt() and 0x0f
        val messageType = (byteBuffer.get(1).toInt() and 0xff) shr 4
        val messageTypeSpecificFlags = byteBuffer.get(1).toInt() and 0x0f
        val serializationMethod = (byteBuffer.get(2).toInt() and 0xff) shr 4
        val messageCompression = byteBuffer.get(2).toInt() and 0x0f
        val reserved = byteBuffer.get(3).toInt() and 0xff

        if (enableLogging) {
            logger.info(
                "binary received: protocolVersion={}, headerSize={}, messageType={}, flags={}, serializationMethod={}, compression={}, reserved={}",
                protocolVersion,
                headerSize,
                messageType,
                messageTypeSpecificFlags,
                serializationMethod,
                messageCompression,
                reserved
            )
        }

        byteBuffer.position(headerSize * 4)

        val fourByte = ByteArray(4)

        when (messageType) {
            11 -> { // Audio-only server response
                if (enableLogging) {
                    logger.info("received audio-only response.")
                }

                if (messageTypeSpecificFlags == 0) {
                    // Ack without audio data
                    if (enableLogging) {
                        logger.info("ack frame without audio payload")
                    }
                    return
                }

                byteBuffer.get(fourByte, 0, 4)
                val sequenceNumber: Int = BigInteger(fourByte).toInt()

                byteBuffer.get(fourByte, 0, 4)
                val payloadSize: Int = BigInteger(fourByte).toInt()

                val payload = ByteArray(payloadSize)
                byteBuffer.get(payload, 0, payloadSize)

                if (sequenceNumber < 0) {
                    // received the last segment, do NOT close websocket (keep long-lived)
                    if (enableLogging) {
                        logger.info("received last audio segment")
                    }

                    this.streamCallback.onCompleted()
                    return
                }

                try {
                    logger.info("received audio segment. length: ${payload.size}")
                    this.streamCallback.onReceived(payload)
                } catch (e: IOException) {
                    this.streamCallback.onError(e)
                    // close connection on serious IO error
                    webSocket.cancel()
                    return
                }
            }

            15 -> { // Error message from server
                byteBuffer.get(fourByte, 0, 4)
                val code: Int = BigInteger(fourByte).toInt()

                byteBuffer.get(fourByte, 0, 4)
                val messageSize: Int = BigInteger(fourByte).toInt()

                val messageBytes = ByteArray(messageSize)
                byteBuffer.get(messageBytes, 0, messageSize)

                val message = String(messageBytes, StandardCharsets.UTF_8)

                if (enableLogging) {
                    logger.error("TTS error: code={}, message={}", code, message)
                }

                this.streamCallback.onError(TTSException(code, message))

                // You may choose to close the WebSocket on server error,
                // or keep it open for the next request, depending on API semantics.
                webSocket.close(WebSocketCloseCode.NORMAL, "error from server")
            }

            else -> {
                if (enableLogging) {
                    logger.warn("received unknown response message type: {}", messageType)
                }
            }
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        if (enableLogging) {
            logger.info("webSocket is closing, code={}, reason={}", code, reason)
        }

        webSocket.close(code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        if (enableLogging) {
            logger.info("connection closed. Code: {}, Reason: {}", code, reason)
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        if (enableLogging) {
            logger.error("webSocket error: {}", t.toString(), t)
        }

        // best effort close
        webSocket.cancel()

        this.streamCallback.onError(t)
    }

    interface StreamCallback {
        fun onSendSuccessful()

        fun onSendFailed()

        fun onReceived(data: ByteArray)

        fun onCompleted()

        fun onError(throwable: Throwable)
    }
}
