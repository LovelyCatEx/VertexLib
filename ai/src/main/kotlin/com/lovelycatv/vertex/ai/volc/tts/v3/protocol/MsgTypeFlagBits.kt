package com.lovelycatv.vertex.ai.volc.tts.v3.protocol

enum class MsgTypeFlagBits(val value: Byte) {
    NO_SEQ(0.toByte()),  // Non-terminating packet without sequence number
    POSITIVE_SEQ(1.toByte()),  // Non-terminating packet with positive sequence number
    LAST_NO_SEQ(2.toByte()),  // Terminating packet without sequence number
    NEGATIVE_SEQ(3.toByte()),  // Terminating packet with negative sequence number
    WITH_EVENT(4.toByte()); // Packet containing event number

    companion object {
        fun fromValue(value: Int): MsgTypeFlagBits {
            for (flag in MsgTypeFlagBits.entries) {
                if (flag.value.toInt() == value) {
                    return flag
                }
            }
            throw IllegalArgumentException("Unknown MsgTypeFlagBits value: $value")
        }
    }
}