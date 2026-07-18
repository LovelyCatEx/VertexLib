package com.lovelycatv.vertex.spider.app.bilibili.misc.types

import com.alibaba.fastjson2.JSON
import java.security.MessageDigest

data class BilibiliWbi(
    val img: String,
    val sub: String,
) {
    private val mixinKey: String
        get() = (this.img + this.sub).let { s ->
            buildString {
                repeat(32) {
                    append(s[MIXIN_KEY_ENC_TAB[it]])
                }
            }
        }

    fun encryptParams(params: Map<String, Any?>): String {
        val sorted = params.filterValues { it != null }.toSortedMap()
        return buildString {
            append(sorted.toQueryString())
            val wts = System.currentTimeMillis() / 1000
            val paramsWithWts = sorted.toMutableMap()
            paramsWithWts["wts"] = wts
            append("&wts=")
            append(wts)
            append("&w_rid=")
            append((paramsWithWts.toQueryString() + mixinKey).toMD5())
        }
    }

    private fun Map<String, Any?>.toQueryString(): String {
        return this.entries.joinToString("&") { (key, value) ->
            val realValue = when (value) {
                is String -> value
                is Byte, Char, Short, Int, Long, Float, Double -> value.toString()
                else -> JSON.toJSONString(value)
            }
            "$key=$realValue"
        }
    }

    private fun String.toMD5(): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(this.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }


    companion object {
        private val MIXIN_KEY_ENC_TAB = intArrayOf(
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
            61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
            36, 20, 34, 44, 52
        )
    }
}