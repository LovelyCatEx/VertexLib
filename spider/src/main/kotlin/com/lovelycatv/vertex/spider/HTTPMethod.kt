package com.lovelycatv.vertex.spider

enum class HTTPMethod {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH;

    companion object {
        fun fromName(name: String): HTTPMethod {
            return HTTPMethod.entries.find { it.name.equals(name, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown HTTPMethod: $name")
        }
    }
}
