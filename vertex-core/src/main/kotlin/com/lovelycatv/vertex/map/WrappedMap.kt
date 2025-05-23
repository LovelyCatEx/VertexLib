package com.lovelycatv.vertex.map

/**
 * @author lovelycat
 * @since 2024-10-27 20:11
 * @version 1.0
 */
open class WrappedMap<K: Any, V: Any?> : MutableMap<K, V> {
    private val map = mutableMapOf<K, V>()

    override fun containsKey(key: K): Boolean = this.map.containsKey(key)

    override fun containsValue(value: V): Boolean = this.map.containsValue(value)

    override fun get(key: K): V? = this.map[key]

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = this.map.entries
    override val keys: MutableSet<K>
        get() = this.map.keys
    override val size: Int
        get() = this.map.size
    override val values: MutableCollection<V>
        get() = this.map.values

    override fun clear() {
        this.map.clear()
    }

    override fun isEmpty(): Boolean = this.map.isEmpty()

    override fun remove(key: K): V? = this.map.remove(key)

    override fun putAll(from: Map<out K, V>) {
        this.map.putAll(from)
    }

    override fun put(key: K, value: V): V = value.also { this.map[key] = it }

    override fun toString(): String {
        val whiteSpaces4 = " ".repeat(4)
        val whiteSpaces8 = whiteSpaces4 + whiteSpaces4
        return this.entries.joinToString(separator = ",\n$whiteSpaces4", prefix = "{\n$whiteSpaces4", postfix = "\n}") {
            "\"${it.key}\": {\n$whiteSpaces8\"@class\": \"${it.key::class.qualifiedName}\",\n$whiteSpaces8\"value\": ${if (it.value is CharSequence) "\"${it.value}\"" else it.value.toString()}\n$whiteSpaces4}"
        }
    }
}