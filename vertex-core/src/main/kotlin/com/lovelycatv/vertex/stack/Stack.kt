package com.lovelycatv.vertex.stack

import com.lovelycatv.vertex.LinearBuffer

class Stack<T> : LinearBuffer<T> {
    private val elements = mutableListOf<T>()

    override fun push(item: T) {
        elements.add(item)
    }

    override fun pop(): T? {
        if (isEmpty()) return null
        return elements.removeAt(elements.size - 1)
    }

    override fun peek(): T? {
        if (isEmpty()) return null
        return elements.last()
    }

    override fun isEmpty(): Boolean = elements.isEmpty()

    override fun size(): Int = elements.size

    override fun toString(): String = elements.toString()
}
