package com.lovelycatv.vertex.ai.mcp

import com.google.gson.annotations.SerializedName

/**
 * @author lovelycat
 * @since 2025-12-16 20:13
 * @version 1.0
 */
data class MCPResponse(
    @SerializedName("jsonrpc")
    val jsonRpc: String,
    val id: Int,
    val result: Result
) {
    data class Result(
        val content: Any
    )
}
