package com.lovelycatv.vertex.ai.volc.tts

/**
 * @author lovelycat
 * @since 2025-12-15 18:12
 * @version 1.0
 */
class TTSException(code: Int, override val message: String?) :
    RuntimeException("code=$code, message=$message")