package com.lovelycatv.vertex.ai.utils

/**
 * @author lovelycat
 * @since 2025-12-17 23:19
 * @version 1.0
 */
class NumberExtensions private constructor()

operator fun Number.plus(other: Number): Number {
    return when {
        this is Double || other is Double ->
            this.toDouble() + other.toDouble()

        this is Float || other is Float ->
            this.toFloat() + other.toFloat()

        this is Long || other is Long ->
            this.toLong() + other.toLong()

        else ->
            this.toInt() + other.toInt()
    }
}

operator fun Number.minus(other: Number): Number {
    return when {
        this is Double || other is Double ->
            this.toDouble() - other.toDouble()

        this is Float || other is Float ->
            this.toFloat() - other.toFloat()

        this is Long || other is Long ->
            this.toLong() - other.toLong()

        else ->
            this.toInt() - other.toInt()
    }
}

operator fun Number.times(other: Number): Number {
    return when {
        this is Double || other is Double ->
            this.toDouble() * other.toDouble()

        this is Float || other is Float ->
            this.toFloat() * other.toFloat()

        this is Long || other is Long ->
            this.toLong() * other.toLong()

        else ->
            this.toInt() * other.toInt()
    }
}

operator fun Number.div(other: Number): Number {
    return when {
        this is Double || other is Double ->
            this.toDouble() / other.toDouble()

        this is Float || other is Float ->
            this.toFloat() / other.toFloat()

        this is Long || other is Long ->
            this.toLong() / other.toLong()

        else ->
            this.toInt() / other.toInt()
    }
}

operator fun Number.compareTo(other: Number): Int {
    return when {
        this is Double || other is Double ->
            this.toDouble().compareTo(other.toDouble())

        this is Float || other is Float ->
            this.toFloat().compareTo(other.toFloat())

        this is Long || other is Long ->
            this.toLong().compareTo(other.toLong())

        else ->
            this.toInt().compareTo(other.toInt())
    }
}