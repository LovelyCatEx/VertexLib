package com.lovelycatv.vertex.spider

data class RequestOptions(
    val userAgent: String = UserAgent.chromeOnWindows().toString(),
    val headers: Map<String, List<String>> = emptyMap(),
    val referer: String? = null,
) {
    fun plainHeaders(): Map<String, String> {
        return this.headers.mapValues {
            it.value.joinToString(", ") { cleanHeaderValue(it) }
        }
    }

    private fun cleanHeaderValue(value: String): String {
        return value
            .replace("\n", "")
            .replace("\r", "")
            .replace("\t", "")
            .trim()
    }
}
