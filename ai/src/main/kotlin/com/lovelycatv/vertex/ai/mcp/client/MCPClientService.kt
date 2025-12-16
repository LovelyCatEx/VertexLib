package com.lovelycatv.vertex.ai.mcp.client

import com.lovelycatv.vertex.ai.mcp.MCPRequest
import com.lovelycatv.vertex.ai.mcp.MCPResponse
import com.lovelycatv.vertex.ai.mcp.MCPToolsListResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * @author lovelycat
 * @since 2025-12-16 20:28
 * @version 1.0
 */
interface MCPClientService {
    @Headers("Content-Type: application/json")
    @POST
    suspend fun listTools(
        @Url url: String,
        @Body body: MCPRequest
    ): MCPToolsListResponse

    @Headers("Content-Type: application/json")
    @POST
    suspend fun callTool(
        @Url url: String,
        @Body body: MCPRequest
    ): MCPResponse
}