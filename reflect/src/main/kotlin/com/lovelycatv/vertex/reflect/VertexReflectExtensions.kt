package com.lovelycatv.vertex.reflect

import java.lang.reflect.Constructor

/**
 * @author lovelycat
 * @since 2025-06-26 19:06
 * @version 1.0
 */
class VertexReflectExtensions private constructor()

fun <T: Any> T.shallowCopy() = ReflectUtils.shallowCopy(this)

fun <T: Any> T.deepCopy() = ReflectUtils.deepCopy(this)

fun <T> Class<T>.noArgsConstructor(): Constructor<T>? {
    return try {
        this.getConstructor()
    } catch (_: Exception) {
        null
    }
}

fun Array<out Constructor<*>>.noArgsConstructor(): Constructor<*>? {
    return this.find { it.parameterCount == 0 }
}

fun Collection<Constructor<*>>.noArgsConstructor(): Constructor<*>? {
    return this.find { it.parameterCount == 0 }
}