package com.lovelycatv.vertex.ai.volc.asr

/**
 * @author lovelycat
 * @since 2026-08-01 00:00
 * @version 1.0
 */
class ASRException(code: Int, override val message: String?) :
    RuntimeException("code=$code, message=$message")
