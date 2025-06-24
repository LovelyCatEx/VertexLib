package com.lovelycatv.vertex.coroutines.jvm

import kotlin.coroutines.Continuation

/**
 * @author lovelycat
 * @since 2025-06-25 02:12
 * @version 1.0
 */
fun interface ContinuationConsumer {
    fun accept(continuation: Continuation<Any?>): Any?
}