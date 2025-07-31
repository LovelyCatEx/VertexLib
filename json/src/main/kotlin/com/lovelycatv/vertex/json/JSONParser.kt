package com.lovelycatv.vertex.json

import com.lovelycatv.vertex.json.exception.JsonParseException

/**
 * @author lovelycat
 * @since 2025-08-01 00:28
 * @version 1.0
 */
class JSONParser {
    private var jsonStr: String = "{}"
    private var currentPosition = 0

    fun initialize(jsonStr: String): JSONParser {
        this.jsonStr = jsonStr
        this.currentPosition = 0
        return this
    }

    fun parseObject(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        skipWhitespace()
        expect('{')
        skipWhitespace()
        if (peek() == '}') {
            next()
            return map
        }
        while (true) {
            skipWhitespace()
            val key = parseString()
            skipWhitespace()
            expect(':')
            skipWhitespace()
            val value = parseValue()
            map[key] = value
            skipWhitespace()
            when (peek()) {
                ',' -> {
                    next()
                    skipWhitespace()
                }

                '}' -> {
                    next()
                    return map
                }

                else -> throw JsonParseException(jsonStr, "Expected ',' or '}' at $currentPosition")
            }
        }
    }

    fun parseArray(): List<Any?> {
        val list = mutableListOf<Any?>()
        skipWhitespace()
        expect('[')
        skipWhitespace()
        if (peek() == ']') {
            next()
            return list
        }
        while (true) {
            val value = parseValue()
            list.add(value)
            skipWhitespace()
            when (peek()) {
                ',' -> {
                    next()
                    skipWhitespace()
                }

                ']' -> {
                    next()
                    return list
                }

                else -> throw JsonParseException(jsonStr, "Expected ',' or ']' at $currentPosition")
            }
        }
    }

    private fun parseValue(): Any? {
        return when {
            match("null") -> null
            match("true") -> true
            match("false") -> false
            peek() == '"' -> parseString()
            peek() == '{' -> parseObject()
            peek() == '[' -> parseArray()
            peek().isDigit() || peek() == '-' -> parseNumber()
            else -> throw JsonParseException(jsonStr, "Unexpected char at $currentPosition: ${peek()}")
        }
    }



    private fun parseString(): String {
        expect('"')
        val sb = StringBuilder()
        while (true) {
            val ch = next()
            when (ch) {
                '\\' -> {
                    val escaped = next()
                    sb.append(
                        when (escaped) {
                            '"', '\\', '/' -> escaped
                            'b' -> '\b'
                            'f' -> '\u000C'
                            'n' -> '\n'
                            'r' -> '\r'
                            't' -> '\t'
                            'u' -> {
                                val hex = jsonStr.substring(currentPosition, currentPosition + 4)
                                currentPosition += 4
                                hex.toInt(16).toChar()
                            }

                            else -> throw JsonParseException(jsonStr, "Invalid escape character: \\$escaped")
                        }
                    )
                }

                '"' -> return sb.toString()
                else -> sb.append(ch)
            }
        }
    }

    private fun parseNumber(): Number {
        val start = currentPosition

        if (peek() == '-') next()

        while (peek().isDigit()) next()

        if (peek() == '.') {
            next()
            while (peek().isDigit()) next()
        }

        if (peek() == 'e' || peek() == 'E') {
            next()
            if (peek() == '+' || peek() == '-') next()
            if (!peek().isDigit()) throw JsonParseException(jsonStr, "Invalid exponent at position $currentPosition")
            while (peek().isDigit()) next()
        }

        val numStr = jsonStr.substring(start, currentPosition)
        return try {
            val num = numStr.toDouble()
            if (numStr.matches(Regex("-?\\d+"))) num.toLong() else num
        } catch (e: NumberFormatException) {
            throw JsonParseException(jsonStr, "Invalid number: $numStr")
        }
    }



    private fun match(expected: String): Boolean {
        if (jsonStr.startsWith(expected, currentPosition)) {
            currentPosition += expected.length
            return true
        }
        return false
    }

    private fun expect(c: Char) {
        if (next() != c) throw JsonParseException(jsonStr, ("Expected '$c' at $currentPosition"))
    }

    private fun skipWhitespace() {
        while (currentPosition < jsonStr.length && jsonStr[currentPosition].isWhitespace()) {
            currentPosition++
        }
    }

    private fun peek(): Char {
        return if (currentPosition < jsonStr.length)
            jsonStr[currentPosition]
        else
            0.toChar()
    }

    private fun next(): Char {
        return if (currentPosition < jsonStr.length)
            jsonStr[currentPosition++]
        else
            throw JsonParseException(jsonStr, "Unexpected end of input")
    }
}