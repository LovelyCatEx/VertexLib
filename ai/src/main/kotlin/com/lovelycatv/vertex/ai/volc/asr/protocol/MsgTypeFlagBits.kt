package com.lovelycatv.vertex.ai.volc.asr.protocol

/**
 * Message type specific flags used by the big model streaming ASR (SAUC) protocol.
 *
 * @author lovelycat
 * @since 2026-08-01 00:00
 * @version 1.0
 */
enum class MsgTypeFlagBits(val value: Byte) {
    NO_SEQUENCE(0b0000),           // Non-terminating packet without sequence number
    POS_SEQUENCE(0b0001),          // Non-terminating packet with positive sequence number
    NEG_SEQUENCE(0b0010),          // Terminating packet without sequence number
    NEG_WITH_SEQUENCE(0b0011);     // Terminating packet with negative sequence number

    companion object {
        @JvmStatic
        fun fromValue(value: Int): MsgTypeFlagBits {
            for (flag in entries) {
                if (flag.value.toInt() == value) {
                    return flag
                }
            }
            throw IllegalArgumentException("Unknown MsgTypeFlagBits value: $value")
        }
    }
}
