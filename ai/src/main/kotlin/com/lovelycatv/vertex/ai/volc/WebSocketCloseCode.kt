package com.lovelycatv.vertex.ai.volc

/**
 * @author lovelycat
 * @since 2025-12-15 18:21
 * @version 1.0
 */
object WebSocketCloseCode {
    const val NORMAL = 1000               // Normal closure
    const val GOING_AWAY = 1001           // Endpoint is going away (e.g., server shutdown or client leave)
    const val PROTOCOL_ERROR = 1002       // Protocol error
    const val UNSUPPORTED_DATA = 1003     // Unsupported data type
    const val NO_STATUS = 1005            // No status received (must not be used in close frame)
    const val ABNORMAL_CLOSE = 1006       // Abnormal closure (connection lost, must not be used in close frame)
    const val INVALID_PAYLOAD = 1007      // Invalid message payload
    const val POLICY_VIOLATION = 1008     // Policy violation
    const val MESSAGE_TOO_BIG = 1009      // Message too big
    const val MANDATORY_EXTENSION = 1010  // Required extension missing
    const val SERVER_ERROR = 1011         // Internal server error
    const val TLS_FAILURE = 1015          // TLS handshake failure (must not be used in close frame)
}