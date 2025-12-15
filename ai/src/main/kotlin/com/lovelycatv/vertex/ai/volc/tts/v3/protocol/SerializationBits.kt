package com.lovelycatv.vertex.ai.volc.tts.v3.protocol

enum class SerializationBits(val value: Byte) {
    Raw(0.toByte()),
    JSON(1.toByte()),
    Thrift(3.toByte()),
    Custom(15.toByte());

    companion object {
        @JvmStatic
        fun fromValue(value: Int): SerializationBits {
            for (type in SerializationBits.entries) {
                if (type.value.toInt() == value) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown SerializationBits value: " + value)
        }
    }
}
