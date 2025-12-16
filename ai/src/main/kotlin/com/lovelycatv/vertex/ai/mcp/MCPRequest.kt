package com.lovelycatv.vertex.ai.mcp

import com.google.gson.annotations.SerializedName

/**
 * @author lovelycat
 * @since 2025-12-16 20:11
 * @version 1.0
 */
data class MCPRequest(
    @SerializedName("jsonrpc")
    val jsonRpc: String = "jsonrpc",
    val id: Int,
    val method: String,
    val params: Params? = null,
) {
    companion object {
        fun listTools(id: Int): MCPRequest {
            return MCPRequest(id = id, method = "tools/list")
        }

        fun callTool(id: Int, params: Params?): MCPRequest {
            return MCPRequest(id = id, method = "tools/call", params = params)
        }
    }
    data class Params(
        val name: String,
        val arguments: Map<String, Any>
    )
}
