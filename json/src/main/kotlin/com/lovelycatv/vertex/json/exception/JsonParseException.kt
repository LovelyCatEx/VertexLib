package com.lovelycatv.vertex.json.exception

/**
 * @author lovelycat
 * @since 2025-08-01 00:50
 * @version 1.0
 */
class JsonParseException(jsonStr: String, message: String) : RuntimeException("Could not parse json: $jsonStr, message: $message")