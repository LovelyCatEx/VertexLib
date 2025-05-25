package com.lovelycatv.vertex.map

/**
 * @author lovelycat
 * @since 2024-10-27 20:11
 * @version 1.0
 */
open class MutableWrappedMap<K: Any, V: Any?>() : MutableMap<K, V> {
    protected val _map = mutableMapOf<K, V>()
    val map: Map<K, V> get() = this._map

    constructor(initialMap: Map<K, V>) : this() {
        this._map.putAll(initialMap)
    }

    override fun containsKey(key: K): Boolean = this._map.containsKey(key)

    override fun containsValue(value: V): Boolean = this._map.containsValue(value)

    override fun get(key: K): V? = this._map[key]

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = this._map.entries
    override val keys: MutableSet<K>
        get() = this._map.keys
    override val size: Int
        get() = this._map.size
    override val values: MutableCollection<V>
        get() = this._map.values

    override fun clear() {
        this._map.clear()
    }

    override fun isEmpty(): Boolean = this._map.isEmpty()

    override fun remove(key: K): V? = this._map.remove(key)

    override fun putAll(from: Map<out K, V>) {
        this._map.putAll(from)
    }

    override fun put(key: K, value: V): V = value.also { this._map[key] = it }

    fun getString(key: K): String? = this[key] as? String

    fun getShort(key: K): Short? = this[key] as? Short

    fun getInt(key: K): Int? = this[key] as? Int

    fun getLong(key: K): Long? = this[key] as? Long

    fun getFloat(key: K): Float? = this[key] as? Float

    fun getDouble(key: K): Double? = this[key] as? Double

    fun getByte(key: K): Byte? = this[key] as? Byte

    override fun toString(): String {
        val whiteSpaces4 = " ".repeat(4)
        val whiteSpaces8 = whiteSpaces4 + whiteSpaces4
        return this.entries.joinToString(separator = ",\n$whiteSpaces4", prefix = "{\n$whiteSpaces4", postfix = "\n}") {
            "\"${it.key}\": {\n$whiteSpaces8\"@class\": \"${it.key::class.qualifiedName}\",\n$whiteSpaces8\"value\": ${if (it.value is CharSequence) "\"${it.value}\"" else it.value.toString()}\n$whiteSpaces4}"
        }
    }
}