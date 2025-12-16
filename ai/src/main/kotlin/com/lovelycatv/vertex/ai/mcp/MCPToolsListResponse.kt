package com.lovelycatv.vertex.ai.mcp

import com.google.gson.annotations.SerializedName

/**
 * @author lovelycat
 * @since 2025-12-16 20:24
 * @version 1.0
 */
data class MCPToolsListResponse(
    @SerializedName("jsonrpc")
    val jsonRpc: String,
    val id: Int,
    val result: Result,
) {
    data class Result(
        val tools: List<MCPFunction>
    )
}
