package com.lovelycatv.vertex.ai.volc.tts.v3.protocol

enum class MsgType(val value: Byte) {
    INVALID(0.toByte()),
    FULL_CLIENT_REQUEST(1.toByte()),
    AUDIO_ONLY_CLIENT(2.toByte()),
    FULL_SERVER_RESPONSE(9.toByte()),
    AUDIO_ONLY_SERVER(11.toByte()),
    FRONT_END_RESULT_SERVER(12.toByte()),
    ERROR(15.toByte());

    companion object {
        fun fromValue(value: Int): MsgType {
            for (type in MsgType.entries) {
                if (type.value.toInt() == value) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown MsgType value: $value")
        }
    }
}