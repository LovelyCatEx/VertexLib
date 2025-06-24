package com.lovelycatv.vertex.coroutines

import kotlinx.coroutines.delay

/**
 * @author lovelycat
 * @since 2025-06-25 02:37
 * @version 1.0
 */
object TestUtils {
    suspend fun noSuspendFunction(): String {
        return "NO_SUSPEND"
    }

    suspend fun delayFunction(delay: Long): String {
        delay(delay)
        return "DELAYED_${delay}"
    }
}