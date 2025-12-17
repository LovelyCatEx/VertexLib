package com.lovelycatv.vertex.ai.utils

import com.google.gson.Gson

/**
 * @author lovelycat
 * @since 2025-12-17 17:23
 * @version 1.0
 */
class GsonExtensions private constructor()

inline fun <reified T> Map<String, Any?>.parseObject(gson: Gson = Gson()): T {
    return gson.fromJson(gson.toJsonTree(this), T::class.java)
}