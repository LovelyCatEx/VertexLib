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