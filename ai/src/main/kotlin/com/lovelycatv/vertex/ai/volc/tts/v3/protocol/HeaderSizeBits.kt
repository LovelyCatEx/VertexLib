package com.lovelycatv.vertex.ai.volc.tts.v3.protocol

enum class HeaderSizeBits(val value: Byte) {
    HeaderSize4(1.toByte()),
    HeaderSize8(2.toByte()),
    HeaderSize12(3.toByte()),
    HeaderSize16(4.toByte());

    companion object {
        @JvmStatic
        fun fromValue(value: Int): HeaderSizeBits {
            for (type in HeaderSizeBits.entries) {
                if (type.value.toInt() == value) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown HeaderSizeBits value: $value")
        }
    }
}
