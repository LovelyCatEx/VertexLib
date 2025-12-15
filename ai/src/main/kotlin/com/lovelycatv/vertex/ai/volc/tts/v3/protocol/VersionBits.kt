package com.lovelycatv.vertex.ai.volc.tts.v3.protocol

enum class VersionBits(val value: Byte) {
    Version1(1.toByte()),
    Version2(2.toByte()),
    Version3(3.toByte()),
    Version4(4.toByte());

    companion object {
        fun fromValue(value: Int): VersionBits {
            for (type in VersionBits.entries) {
                if (type.value.toInt() == value) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown VersionBits value: $value")
        }
    }
}
