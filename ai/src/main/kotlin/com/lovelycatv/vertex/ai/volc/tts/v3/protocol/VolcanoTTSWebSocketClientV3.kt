package com.lovelycatv.vertex.ai.volc.tts.v3.protocol

import com.lovelycatv.vertex.ai.volc.tts.TTSException
import com.lovelycatv.vertex.ai.volc.tts.v3.VolcanoTTSResponseV3
import com.lovelycatv.vertex.log.logger
import okhttp3.*
import okio.ByteString
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.BlockingQueue
import java.nio.charset.StandardCharsets
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author lovelycat
 * @since 2025-12-16 01:10
 * @version 1.0
 */
class VolcanoTTSWebSocketClientV3(
    private val apiUrl: String,
    private val client: OkHttpClient,
    val callback: StreamCallbackV3,
    private val enableLogging: Boolean = false
) : WebSocketListener() {

    private val logger = logger()

    @Volatile
    private lateinit var webSocket: WebSocket

    /** Queue storing all received Message objects */
    private val messageQueue: BlockingQueue<Message> = LinkedBlockingQueue()

    init {
        initializeConnection()
    }

    fun initializeConnection() {
        if (enableLogging) {
            logger.info("Connecting to TTS v3 WebSocket: $apiUrl")
        }

        val request = Request.Builder()
            .url(apiUrl)
            .build()

        this.webSocket = client.newWebSocket(request, this)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        if (enableLogging) {
            logger.info("WebSocket connected. Logid=${response.header("x-tt-logid")}")
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
            val msg = Message.unmarshal(bytes.toByteArray())

            if (enableLogging) {
                logger.info("Received Message: type=${msg.type}, event=${msg.event}, size=${msg.payload.size}")
            }

            messageQueue.put(msg)
            callback.onMessage(msg)
        } catch (e: Exception) {
            logger.error("Failed to parse message", e)
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
        if (enableLogging) {
            logger.error("WebSocket failure", t)
        }

        callback.onError(t)
    }


    fun sendStartConnection() {
        val msg = Message(MsgType.FULL_CLIENT_REQUEST, MsgTypeFlagBits.WITH_EVENT).apply {
            event = EventType.START_CONNECTION
            payload = "{}".toByteArray(StandardCharsets.UTF_8)
        }

        this.sendMessage(msg)
    }

    fun sendFinishConnection() {
        val msg = Message(MsgType.FULL_CLIENT_REQUEST, MsgTypeFlagBits.WITH_EVENT).apply {
            event = EventType.FINISH_CONNECTION
            payload = "{}".toByteArray()
        }

        this.sendMessage(msg)
    }

    fun sendStartSession(payload: String, sessionId: String) {
        this.sendStartSession(payload.toByteArray(), sessionId)
    }

    fun sendStartSession(payload: ByteArray, sessionId: String) {
        val msg = Message(MsgType.FULL_CLIENT_REQUEST, MsgTypeFlagBits.WITH_EVENT).apply {
            event = EventType.START_SESSION
            this.sessionId = sessionId
            this.payload = payload
        }

        this.sendMessage(msg)
    }

    fun sendFinishSession(sessionId: String) {
        val msg = Message(MsgType.FULL_CLIENT_REQUEST, MsgTypeFlagBits.WITH_EVENT).apply {
            event = EventType.FINISH_SESSION
            this.sessionId = sessionId
            payload = "{}".toByteArray()
        }

        this.sendMessage(msg)
    }

    fun sendTaskRequest(payload: String, sessionId: String) {
        this.sendTaskRequest(payload.toByteArray(), sessionId)
    }

    fun sendTaskRequest(payload: ByteArray, sessionId: String) {
        val msg = Message(MsgType.FULL_CLIENT_REQUEST, MsgTypeFlagBits.WITH_EVENT).apply {
            event = EventType.TASK_REQUEST
            this.sessionId = sessionId
            this.payload = payload
        }

        this.sendMessage(msg)
    }

    fun sendFullClientMessage(payload: ByteArray) {
        val msg = Message(MsgType.FULL_CLIENT_REQUEST, MsgTypeFlagBits.NO_SEQ).apply {
            this.payload = payload
        }

        this.sendMessage(msg)
    }

    private fun sendMessage(message: Message) {
        if (enableLogging) {
            logger.info("Sending Message: $message")
        }

        val sent = webSocket.send(ByteString.of(*message.marshal()))

        if (sent) {
            callback.onSendSuccessfully(message)
        } else {
            callback.onSendFailed(message)
        }
    }


    fun receiveMessage(): Message {
        val msg = messageQueue.take()
        if (enableLogging) {
            logger.info("Dequeued Message: $msg")
        }

        return msg
    }

    fun waitForMessage(type: MsgType, event: EventType): Message {
        while (true) {
            val msg = receiveMessage()
            if (msg.type == type && msg.event == event) {
                return msg
            } else {
                throw TTSException(-1, "Unexpected message received: $msg")
            }
        }
    }


    interface StreamCallbackV3 {
        /** WebSocket connected */
        fun onConnected()

        /** Any message received (already parsed as Message) */
        fun onMessage(message: Message)

        /** Any message successfully sent */
        fun onSendSuccessfully(message: Message)

        /** Any message send failed */
        fun onSendFailed(message: Message)

        /** Connection closing */
        fun onClosing(code: Int, reason: String)

        /** Connection closed */
        fun onClosed(code: Int, reason: String)

        /** Any error happened */
        fun onError(t: Throwable)

        /** Audio process completed */
        fun onCompleted(
            sentence: VolcanoTTSResponseV3.Sentence,
            usage: VolcanoTTSResponseV3.Usage
        )

        /** Audio segment received */
        fun onReceived(payload: ByteArray)
    }
}
