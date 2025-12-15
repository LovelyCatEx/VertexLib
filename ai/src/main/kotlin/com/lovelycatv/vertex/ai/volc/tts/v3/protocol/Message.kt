package com.lovelycatv.vertex.ai.volc.tts.v3.protocol

import okhttp3.internal.and
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

class Message(
    val type: MsgType,
    val flag: MsgTypeFlagBits
) {
    var version = VersionBits.Version1.value
    var headerSize = HeaderSizeBits.HeaderSize4.value
    var serialization = SerializationBits.JSON.value
    var compression: Byte = 0
    var event: EventType? = null
    var sessionId: String? = null
    var connectId: String? = null
    var sequence = 0
    var errorCode = 0
    var payload: ByteArray = ByteArray(0)

    @Throws(Exception::class)
    fun marshal(): ByteArray {
        val buffer = ByteArrayOutputStream()

        // Write header
        buffer.write((version.toInt() and 0x0F) shl 4 or (headerSize.toInt() and 0x0F))
        buffer.write((type.value and 0x0F) shl 4 or (flag.value and 0x0F))
        buffer.write((serialization.toInt() and 0x0F) shl 4 or (compression.toInt() and 0x0F))

        val headerSizeInt = 4 * headerSize.toInt()
        var padding = headerSizeInt - buffer.size()
        while (padding > 0) {
            buffer.write(0)
            padding -= 1
        }

        // Write event if present
        event?.let {
            val eventBytes = ByteBuffer.allocate(4).putInt(it.value).array()
            buffer.write(eventBytes)
        }

        // Write sessionId if present
        sessionId?.let {
            val sessionIdBytes = it.toByteArray(StandardCharsets.UTF_8)
            buffer.write(ByteBuffer.allocate(4).putInt(sessionIdBytes.size).array())
            buffer.write(sessionIdBytes)
        }

        // Write connectId if present
        connectId?.let {
            val connectIdBytes = it.toByteArray(StandardCharsets.UTF_8)
            buffer.write(ByteBuffer.allocate(4).putInt(connectIdBytes.size).array())
            buffer.write(connectIdBytes)
        }

        // Write sequence if present
        if (sequence != 0) {
            buffer.write(ByteBuffer.allocate(4).putInt(sequence).array())
        }

        // Write errorCode if present
        if (errorCode != 0) {
            buffer.write(ByteBuffer.allocate(4).putInt(errorCode).array())
        }

        // Write payload if present
        if (payload.isNotEmpty()) {
            buffer.write(ByteBuffer.allocate(4).putInt(payload.size).array())
            buffer.write(payload)
        }

        return buffer.toByteArray()
    }

    override fun toString(): String {
        when (this.type) {
            MsgType.AUDIO_ONLY_SERVER, MsgType.AUDIO_ONLY_CLIENT -> {
                if (this.flag == MsgTypeFlagBits.POSITIVE_SEQ || this.flag == MsgTypeFlagBits.NEGATIVE_SEQ) {
                    return String.format(
                        "MsgType: %s, EventType: %s, Sequence: %d, PayloadSize: %d, SessionId: %s",
                        this.type,
                        this.event,
                        this.sequence,
                        this.payload.size,
                        this.sessionId
                    )
                }
                return String.format(
                    "MsgType: %s, EventType: %s, PayloadSize: %d, SessionId: %s", this.type, this.event,
                    this.payload.size,
                    this.sessionId
                )
            }

            MsgType.ERROR -> return String.format(
                "MsgType: %s, EventType: %s, ErrorCode: %d, Payload: %s, SessionId: %s", this.type, this.event, this.errorCode,
                String(this.payload),
                this.sessionId
            )

            else -> {
                if (this.flag == MsgTypeFlagBits.POSITIVE_SEQ || this.flag == MsgTypeFlagBits.NEGATIVE_SEQ) {
                    return String.format(
                        "MsgType: %s, EventType: %s, Sequence: %d, Payload: %s, SessionId: %s",
                        this.type, this.event, this.sequence,
                        String(this.payload),
                        this.sessionId
                    )
                }
                return String.format(
                    "MsgType: %s, EventType: %s, Payload: %s, SessionId: %s", this.type, this.event,
                    String(this.payload),
                    this.sessionId
                )
            }
        }
    }

    companion object {
        @JvmStatic
        @Throws(Exception::class)
        fun unmarshal(data: ByteArray): Message {
            val buffer = ByteBuffer.wrap(data)

            val typeAndFlag = data[1]
            val type = MsgType.fromValue((typeAndFlag.toInt() shr 4) and 0x0F)
            val flag = MsgTypeFlagBits.fromValue(typeAndFlag.toInt() and 0x0F)

            // Read version and header size
            val versionAndHeaderSize = buffer.get().toInt()
            val version = VersionBits.fromValue((versionAndHeaderSize shr 4) and 0x0F)
            val headerSize = HeaderSizeBits.fromValue(versionAndHeaderSize and 0x0F)

            // Skip second byte
            buffer.get()

            // Read serialization and compression method
            val serializationCompression = buffer.get().toInt()
            val serialization = SerializationBits.fromValue((serializationCompression shr 4) and 0x0F)
            val compression = CompressionBits.fromValue(serializationCompression and 0x0F)

            // Skip padding bytes
            val headerSizeInt = 4 * headerSize.value.toInt()
            var paddingSize = headerSizeInt - 3
            while (paddingSize > 0) {
                buffer.get()
                paddingSize -= 1
            }

            val message = Message(type, flag)
            message.version = version.value
            message.headerSize = headerSize.value
            message.serialization = serialization.value
            message.compression = compression.value

            // Read sequence if present
            if (flag == MsgTypeFlagBits.POSITIVE_SEQ || flag == MsgTypeFlagBits.NEGATIVE_SEQ) {
                // Read 4 bytes from ByteBuffer and parse as int (big-endian)
                val sequeueBytes = ByteArray(4)
                if (buffer.remaining() >= 4) {
                    buffer.get(sequeueBytes) // Read 4 bytes into array
                    val wrapper = ByteBuffer.wrap(sequeueBytes)
                    wrapper.order(ByteOrder.BIG_ENDIAN) // Set big-endian order
                    message.sequence = wrapper.getInt()
                }
            }

            // Read event if present
            if (flag == MsgTypeFlagBits.WITH_EVENT) {
                // Read 4 bytes from ByteBuffer and parse as int (big-endian)
                val eventBytes = ByteArray(4)
                if (buffer.remaining() >= 4) {
                    buffer.get(eventBytes) // Read 4 bytes into array
                    val wrapper = ByteBuffer.wrap(eventBytes)
                    wrapper.order(ByteOrder.BIG_ENDIAN) // Set big-endian order
                    message.event = EventType.fromValue(wrapper.getInt())
                }

                if (type != MsgType.ERROR && !(message.event == EventType.START_CONNECTION || message.event == EventType.FINISH_CONNECTION || message.event == EventType.CONNECTION_STARTED || message.event == EventType.CONNECTION_FAILED || message.event == EventType.CONNECTION_FINISHED)) {
                    // Read sessionId if present
                    val sessionIdLength = buffer.getInt()
                    if (sessionIdLength > 0) {
                        val sessionIdBytes = ByteArray(sessionIdLength)
                        buffer.get(sessionIdBytes)
                        message.sessionId = String(sessionIdBytes, StandardCharsets.UTF_8)
                    }
                }

                if (message.event == EventType.CONNECTION_STARTED || message.event == EventType.CONNECTION_FAILED || message.event == EventType.CONNECTION_FINISHED) {
                    // Read connectId if present
                    val connectIdLength = buffer.getInt()
                    if (connectIdLength > 0) {
                        val connectIdBytes = ByteArray(connectIdLength)
                        buffer.get(connectIdBytes)
                        message.connectId = String(connectIdBytes, StandardCharsets.UTF_8)
                    }
                }
            }

            // Read errorCode if present
            if (type == MsgType.ERROR) {
                // Read 4 bytes from ByteBuffer and parse as int (big-endian)
                val errorCodeBytes = ByteArray(4)
                if (buffer.remaining() >= 4) {
                    buffer.get(errorCodeBytes) // Read 4 bytes into array
                    val wrapper = ByteBuffer.wrap(errorCodeBytes)
                    wrapper.order(ByteOrder.BIG_ENDIAN) // Set big-endian order
                    message.errorCode = wrapper.getInt()
                }
            }

            // Read remaining bytes as payload
            if (buffer.remaining() > 0) {
                // 4 bytes length
                val payloadLength = buffer.getInt()
                if (payloadLength > 0) {
                    val payloadBytes = ByteArray(payloadLength)
                    buffer.get(payloadBytes)
                    message.payload = payloadBytes
                }
            }

            return message
        }
    }
}