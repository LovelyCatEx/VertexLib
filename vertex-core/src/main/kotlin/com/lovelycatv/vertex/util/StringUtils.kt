package com.lovelycatv.vertex.util

/**
 * @author lovelycat
 * @since 2025-08-07 21:35
 * @version 1.0
 */
object StringUtils {
    /**
     * Replaces the specified placeholders in the given text with the provided replacement strings, in order.
     *
     * This function scans the input [text] and replaces each occurrence of the [placeholders] (in the given order of appearance)
     * with the corresponding string from [replacements]. Replacements are applied sequentially, and each matched placeholder
     * consumes one replacement value.
     *
     * If there are more placeholders matched than available [replacements], the unmatched placeholders are left unchanged.
     * If there are more replacements than placeholders matched, the extra replacements are ignored.
     *
     * Example:
     * ```
     * val result = orderReplace("Hello $T and $N", arrayOf("\$T", "\$N"), "Alice", "Bob")
     * // result: "Hello Alice and Bob"
     * ```
     *
     * @param text The original input text containing placeholders.
     * @param placeholders An array of placeholder patterns (as plain strings, not regex), e.g., arrayOf("\$T", "\$N").
     * @param replacements A variable number of strings to replace the placeholders in order.
     * @return A new string with placeholders replaced by the corresponding values from [replacements].
     */
    @JvmStatic
    fun orderReplace(text: CharSequence, placeholders: Array<String>, vararg replacements: CharSequence): String {
        val regex = placeholders.joinToString(separator = "|", prefix = "", postfix = "") { Regex.escape(it) }.toRegex()
        var index = 0
        return regex.replace(text) {
            if (index < replacements.size) {
                val replacement = replacements[index]
                index++
                replacement
            } else {
                it.value
            }
        }
    }


}