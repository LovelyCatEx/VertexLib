package com.lovelycatv.vertex

/**
 * @author lovelycat
 * @since 2025-08-02 13:52
 * @version 1.0
 */
interface LinearBuffer<T> {
    fun push(item: T)

    fun pop(): T?

    fun peek(): T?

    fun isEmpty(): Boolean

    fun size(): Int
}