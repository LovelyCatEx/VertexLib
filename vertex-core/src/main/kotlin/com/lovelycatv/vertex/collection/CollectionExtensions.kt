package com.lovelycatv.vertex.collection

/**
 * @author lovelycat
 * @since 2025-05-25 15:55
 * @version 1.0
 */
class CollectionExtensions private constructor()

fun <A, B> Iterable<A>.merge(other: Iterable<B>, transformer: (B) -> A): List<A> {
    return this.toMutableList().apply {
        addAll(other.map(transformer))
    }
}

fun <A, B> Iterable<A>.mergeTo(other: Iterable<B>, transformer: (A) -> B): List<B> {
    return other.merge(this, transformer)
}

fun <A, B> Iterable<A>.leftJoin(other: Iterable<B>, on: (A, B) -> Boolean): Map<A, List<B>> {
    val resultMap = mutableMapOf<A, List<B>>()

    for (a in this) {
        val set = mutableListOf<B>()
        resultMap[a] = set
        for (b in other) {
            if (on.invoke(a, b)) {
                set.add(b)
            }
        }
    }

    return resultMap
}

fun <A, B> Iterable<A>.rightJoin(other: Iterable<B>, on: (B, A) -> Boolean): Map<B, List<A>> {
    return other.leftJoin(this, on)
}

fun <E> Collection<E>.indexesOf(value: E): Set<Int> {
    val result = mutableSetOf<Int>()
    for ((index, e) in this.withIndex()) {
        if (e == value) {
            result.add(index)
        }
    }
    return result
}

fun <E> MutableCollection<E>.removeAt(indexes: Set<Int>): List<E> {
    val removed = mutableListOf<E>()

    val it = this.iterator()
    for ((index, e) in it.withIndex()) {
        if (index in indexes) {
            it.remove()
            removed.add(e)
        }
    }

    return removed
}

fun <E> MutableCollection<E>.removeAt(indexes: Iterable<Int>): List<E> {
    return this.removeAt(indexes.toSet())
}


fun <E> MutableCollection<E>.removeAt(vararg indexes: Int): List<E> {
    return this.removeAt(indexes.toSet())
}

fun <A> Iterable<A>.divide(filterOut: (A) -> Boolean): Pair<List<A>, List<A>> {
    val left = mutableListOf<A>()
    val right = mutableListOf<A>()
    for (a in this) {
        if (filterOut.invoke(a)) {
            right.add(a)
        } else {
            left.add(a)
        }
    }

    return left to right
}
