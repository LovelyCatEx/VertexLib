package com.lovelycatv.vertex.ai.volc.asr.protocol

/**
 * Message types used by the big model streaming ASR (SAUC) protocol.
 *
 * @author lovelycat
 * @since 2026-08-01 00:00
 * @version 1.0
 */
enum class MsgType(val value: Byte) {
    CLIENT_FULL_REQUEST(0b0001),
    CLIENT_AUDIO_ONLY_REQUEST(0b0010),
    SERVER_FULL_RESPONSE(0b1001),
    SERVER_ACK(0b1011),
    SERVER_ERROR_RESPONSE(0b1111);

    companion object {
        @JvmStatic
        fun fromValue(value: Int): MsgType {
            for (type in entries) {
                if (type.value.toInt() == value) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown MsgType value: $value")
        }
    }
}
