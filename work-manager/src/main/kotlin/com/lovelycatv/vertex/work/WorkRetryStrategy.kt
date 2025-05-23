package com.lovelycatv.vertex.work

/**
 * @author lovelycat
 * @since 2024-10-31 19:04
 * @version 1.0
 */
typealias RetryIntervalSupplier = (retriedTimes: Int) -> Long

data class WorkRetryStrategy(
    val type: Type,
    val retryInterval: RetryIntervalSupplier,
    val maxRetryTimes: Int
) {
    enum class Type {
        NO_RETRY,
        RETRY
    }
}