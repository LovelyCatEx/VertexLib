package com.lovelycatv.vertex.ai.network

import com.lovelycatv.vertex.ai.volc.WebSocketCloseCode
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okio.ByteString

/**
 * @author lovelycat
 * @since 2025-12-16 03:26
 * @version 1.0
 */
open class VertexWebSocket(
    private val webSocketUrl: String,
    private val client: OkHttpClient,
) : StatefulWebSocketListener() {
    private var webSocket: WebSocket? = null

    fun connect() {
        if (this.webSocketStatus == WebSocketStatus.CONNECTING) {
            return
        }

        if (this.webSocketStatus == WebSocketStatus.CONNECTED || this.webSocket != null) {
            this.close("disconnect")
        }

        val request: Request = Request.Builder()
            .url(webSocketUrl)
            .build()

        this.webSocketStatus = WebSocketStatus.CONNECTING

        // create WebSocket connection (async, but usually fast)
        this.webSocket = client.newWebSocket(request, this)
    }

    private fun autoCheckConnectionAndReconnect() {
        if (this.webSocketStatus != WebSocketStatus.CONNECTED || this.webSocket == null) {
            this.connect()
        }
    }

    fun send(bytes: ByteString): Boolean {
        return this.send(bytes.toByteArray())
    }

    fun send(bytes: ByteArray): Boolean {
        this.autoCheckConnectionAndReconnect()

        return this.webSocket?.send(ByteString.of(*bytes))
            ?: throw IllegalStateException("The webSocket connection is invalid")
    }

    fun close(reason: String): Boolean {
        return try {
            this.webSocket?.close(WebSocketCloseCode.NORMAL, reason)
            this.webSocket?.cancel()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}