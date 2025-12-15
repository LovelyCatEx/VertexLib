package com.lovelycatv.vertex.ai.volc.tts.v3.protocol

enum class CompressionBits(val value: Byte) {
    None_(0.toByte()),
    Gzip(1.toByte()),
    Custom(3.toByte());

    companion object {
        @JvmStatic
        fun fromValue(value: Int): CompressionBits {
            for (type in CompressionBits.entries) {
                if (type.value.toInt() == value) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown CompressionBits value: $value")
        }
    }
}
