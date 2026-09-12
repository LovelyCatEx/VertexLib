package com.lovelycatv.vertex.ai.utils

import com.jayway.jsonpath.DocumentContext

class JsonPathExtensions private constructor()

fun <T> DocumentContext.tryRead(path: String): T? {
    return try {
        this.read<T>(path)
    } catch (_: Exception) {
        null
    }
}