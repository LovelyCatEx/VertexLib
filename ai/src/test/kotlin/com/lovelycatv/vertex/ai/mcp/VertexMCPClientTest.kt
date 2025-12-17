package com.lovelycatv.vertex.ai.mcp

import com.lovelycatv.vertex.ai.mcp.client.VertexMCPClient
import com.lovelycatv.vertex.ai.mcp.client.VertexMCPClientConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class VertexMCPClientTest {
    private val vertexMCPClient = VertexMCPClient(
        VertexMCPClientConfig(
            "https://dashscope.aliyuncs.com/api/v1/mcps/",
            System.getProperty("VertexMCPClientTestAccessToken"),
            enableLogging = true
        )
    )

    @Test
    fun listTools() {
        val response = runBlocking {
            vertexMCPClient.listTools("market-cmapi00060393/mcp")
        }

        println(response)
    }

    @Test
    fun callTool() {
        val response = runBlocking {
            vertexMCPClient.callTool(
                "market-cmapi00060393/mcp",
                "获取问题",
                mapOf("version" to "simple")
            )
        }

        println(response)
    }

}