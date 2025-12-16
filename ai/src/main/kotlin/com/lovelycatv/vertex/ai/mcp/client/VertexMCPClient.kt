package com.lovelycatv.vertex.ai.mcp.client

import com.google.gson.GsonBuilder
import com.lovelycatv.vertex.ai.mcp.MCPFunction
import com.lovelycatv.vertex.ai.mcp.MCPRequest
import com.lovelycatv.vertex.ai.mcp.MCPResponse
import com.lovelycatv.vertex.ai.mcp.MCPToolsListResponse
import com.lovelycatv.vertex.ai.mcp.codec.PropertyDeserializer
import com.lovelycatv.vertex.ai.network.VertexRetrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author lovelycat
 * @since 2025-12-16 20:25
 * @version 1.0
 */
class VertexMCPClient(
    val config: VertexMCPClientConfig
) {
    private val vertexRetrofit = VertexRetrofit(
        baseUrl = this.config.mcpBaseUrl,
        timeoutSeconds = this.config.timeoutSeconds,
        enableLogging = this.config.enableLogging,
        converterFactory = GsonConverterFactory.create(
            GsonBuilder()
                .registerTypeAdapter(
                    MCPFunction.InputSchema.Property::class.java,
                    PropertyDeserializer()
                )
                .create()
        ),
        preInterceptor = { chain ->
            val request = chain.request().newBuilder()
            request.addHeader("Authorization", "Bearer ${this.config.accessToken}")
            chain.proceed(request.build())
        }
    )

    private val mcpClientService: MCPClientService = this.vertexRetrofit.retrofit.create(MCPClientService::class.java)

    suspend fun listTools(endpoint: String): MCPToolsListResponse {
        return this.mcpClientService.listTools(
            this.buildRequestUrl(endpoint),
            MCPRequest.Companion.listTools(0)
        )
    }

    suspend fun callTool(endpoint: String, functionName: String, arguments: Map<String, Any>): MCPResponse {
        return this.mcpClientService.callTool(
            this.buildRequestUrl(endpoint),
            MCPRequest.Companion.callTool(
                0,
                MCPRequest.Params(
                    name = functionName,
                    arguments = arguments
                )
            )
        )
    }

    private fun buildRequestUrl(endpoint: String): String {
        return this.config.mcpBaseUrl + endpoint
    }
}