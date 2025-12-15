package com.lovelycatv.vertex.ai.network

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * @author lovelycat
 * @since 2025-12-16 03:24
 * @version 1.0
 */
open class StatefulWebSocketListener : WebSocketListener() {
    var webSocketStatus: WebSocketStatus = WebSocketStatus.PREPARED
        protected set

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocketStatus = WebSocketStatus.CONNECTED
        super.onOpen(webSocket, response)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        webSocketStatus = WebSocketStatus.DISCONNECTED
        super.onClosed(webSocket, code, reason)
    }
}