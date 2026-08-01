package com.lovelycatv.vertex.ai.volc.asr.protocol

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Binary protocol frame for the big model streaming ASR (SAUC) service.
 *
 * The wire layout is a 4 byte header followed by optional sequence / event /
 * error-code fields and finally a length-prefixed (gzip compressed) payload.
 * Unlike the TTS v3 [com.lovelycatv.vertex.ai.volc.tts.v3.protocol.Message] the
 * ASR protocol always gzip compresses the JSON payload and carries no sessionId,
 * therefore it keeps its own self-contained marshalling here.
 *
 * @author lovelycat
 * @since 2026-08-01 00:00
 * @version 1.0
 */
class ASRMessage(
    val type: MsgType,
    val flag: MsgTypeFlagBits
) {
    var sequence = 0
    var event = 0
    var isLastPackage = false
    var errorCode = 0
    var payload: ByteArray = ByteArray(0)

    /**
     * Serialize this frame into the wire format. The [payload] is expected to be
     * the already-serialized JSON (or raw audio) bytes; it will be gzip compressed
     * here to stay consistent with the server expectations.
     */
    fun marshal(): ByteArray {
        val buffer = ByteArrayOutputStream()

        // Header
        buffer.write((PROTOCOL_VERSION.toInt() shl 4) or DEFAULT_HEADER_SIZE.toInt())
        buffer.write((type.value.toInt() shl 4) or flag.value.toInt())
        buffer.write((SERIALIZATION_JSON.toInt() shl 4) or COMPRESSION_GZIP.toInt())
        buffer.write(0) // reserved

        // Sequence, when the flag carries one
        if (flag == MsgTypeFlagBits.POS_SEQUENCE || flag == MsgTypeFlagBits.NEG_WITH_SEQUENCE) {
            buffer.write(intToBytes(sequence))
        }

        val compressedPayload = gzipCompress(payload)
        buffer.write(intToBytes(compressedPayload.size))
        buffer.write(compressedPayload)

        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ASRMessage(type=$type, flag=$flag, sequence=$sequence, event=$event, " +
                "isLastPackage=$isLastPackage, errorCode=$errorCode, payloadSize=${payload.size})"
    }

    companion object {
        const val PROTOCOL_VERSION: Byte = 0b0001
        const val DEFAULT_HEADER_SIZE: Byte = 0b0001

        const val SERIALIZATION_JSON: Byte = 0b0001
        const val COMPRESSION_GZIP: Byte = 0b0001

        /**
         * Build the initial full client request that opens a recognition session.
         */
        @JvmStatic
        fun fullClientRequest(payload: ByteArray, sequence: Int): ASRMessage {
            return ASRMessage(MsgType.CLIENT_FULL_REQUEST, MsgTypeFlagBits.POS_SEQUENCE).apply {
                this.sequence = sequence
                this.payload = payload
            }
        }

        /**
         * Build an audio-only request carrying a single PCM segment.
         *
         * @param isLast whether this is the terminating packet; the server expects
         *               a negative sequence number for the last segment.
         */
        @JvmStatic
        fun audioOnlyRequest(payload: ByteArray, sequence: Int, isLast: Boolean): ASRMessage {
            val flag = if (isLast) MsgTypeFlagBits.NEG_WITH_SEQUENCE else MsgTypeFlagBits.POS_SEQUENCE
            return ASRMessage(MsgType.CLIENT_AUDIO_ONLY_REQUEST, flag).apply {
                this.sequence = if (isLast) -sequence else sequence
                this.isLastPackage = isLast
                this.payload = payload
            }
        }

        /**
         * Parse a server frame into an [ASRMessage]. The payload, if gzip compressed,
         * is decompressed so [ASRMessage.payload] always holds the plain (JSON) bytes.
         */
        @JvmStatic
        fun unmarshal(data: ByteArray): ASRMessage {
            require(data.size >= 4) { "Frame too short: ${data.size} bytes" }

            val headerSize = data[0].toInt() and 0x0F
            val messageType = (data[1].toInt() shr 4) and 0x0F
            val specificFlags = data[1].toInt() and 0x0F
            val serialization = (data[2].toInt() shr 4) and 0x0F
            val compression = data[2].toInt() and 0x0F

            val type = MsgType.fromValue(messageType)
            val flag = MsgTypeFlagBits.fromValue(specificFlags)
            val message = ASRMessage(type, flag)

            var payload = data.copyOfRange(headerSize * 4, data.size)

            // Sequence present
            if (specificFlags and 0x01 != 0) {
                message.sequence = bytesToInt(payload.copyOfRange(0, 4))
                payload = payload.copyOfRange(4, payload.size)
            }
            // Last package
            if (specificFlags and 0x02 != 0) {
                message.isLastPackage = true
            }
            // Event present
            if (specificFlags and 0x04 != 0) {
                message.event = bytesToInt(payload.copyOfRange(0, 4))
                payload = payload.copyOfRange(4, payload.size)
            }

            when (type) {
                MsgType.SERVER_FULL_RESPONSE, MsgType.SERVER_ACK -> {
                    // 4 bytes payload size
                    payload = payload.copyOfRange(4, payload.size)
                }
                MsgType.SERVER_ERROR_RESPONSE -> {
                    message.errorCode = bytesToInt(payload.copyOfRange(0, 4))
                    // followed by 4 bytes payload size
                    payload = payload.copyOfRange(8, payload.size)
                }
                else -> {}
            }

            if (payload.isEmpty()) {
                return message
            }

            if (compression == COMPRESSION_GZIP.toInt()) {
                payload = gzipDecompress(payload)
            }

            // serialization == JSON (or raw) -> keep raw bytes for the caller to parse
            message.payload = payload
            return message
        }

        private fun intToBytes(value: Int): ByteArray {
            return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array()
        }

        private fun bytesToInt(src: ByteArray): Int {
            require(src.size == 4) { "Invalid byte array for int conversion" }
            return ByteBuffer.wrap(src).order(ByteOrder.BIG_ENDIAN).int
        }

        private fun gzipCompress(src: ByteArray): ByteArray {
            // Note: an empty payload must still produce a valid gzip stream (~20 bytes),
            // otherwise the server fails to ungzip the terminating packet with an EOF.
            val out = ByteArrayOutputStream()
            GZIPOutputStream(out).use { it.write(src) }
            return out.toByteArray()
        }

        private fun gzipDecompress(src: ByteArray): ByteArray {
            if (src.isEmpty()) return ByteArray(0)
            val out = ByteArrayOutputStream()
            GZIPInputStream(ByteArrayInputStream(src)).use { gzip ->
                val buffer = ByteArray(4096)
                var len: Int
                while (gzip.read(buffer).also { len = it } > 0) {
                    out.write(buffer, 0, len)
                }
            }
            return out.toByteArray()
        }
    }
}
