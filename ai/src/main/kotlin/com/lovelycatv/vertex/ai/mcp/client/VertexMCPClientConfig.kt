package com.lovelycatv.vertex.ai.mcp.client

/**
 * @author lovelycat
 * @since 2025-12-16 20:25
 * @version 1.0
 */
data class VertexMCPClientConfig(
    val mcpBaseUrl: String,
    val accessToken: String,
    val timeoutSeconds: Long = 60,
    val enableLogging: Boolean = false,
)
