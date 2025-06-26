package com.lovelycatv.vertex.reflect

/**
 * @author lovelycat
 * @since 2025-06-26 19:06
 * @version 1.0
 */
class VertexReflectExtensions private constructor()

fun <T: Any> T.shallowCopy() = ReflectUtils.shallowCopy(this)

fun <T: Any> T.deepCopy() = ReflectUtils.deepCopy(this)
