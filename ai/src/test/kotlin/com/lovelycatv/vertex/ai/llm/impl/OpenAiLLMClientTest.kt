package com.lovelycatv.vertex.ai.llm.impl

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.llm.ChatRequest
import com.lovelycatv.vertex.ai.llm.config.LLMClientConfig
import com.lovelycatv.vertex.ai.llm.message.UserChatMessage
import com.lovelycatv.vertex.ai.llm.tool.ToolDeclaration
import com.lovelycatv.vertex.ai.llm.tool.parameter.PrimitiveToolParameter
import com.lovelycatv.vertex.ai.llm.tool.parameter.ToolParameter
import com.lovelycatv.vertex.ai.llm.tool.parameter.ToolParameterType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OpenAiLLMClientTest {
    private val gson = Gson()

    private val config = LLMClientConfig(
        baseUrl = "https://api.deepseek.com/v1",
        apiKey = ""
    )

    private val client = OpenAiLLMClient(config)

    private val request = ChatRequest(
        model = "deepseek-flash",
        messages = listOf(
            UserChatMessage("hello, how's the weather today in beijing and shanghai")
        ),
        stream = false,
        tools = listOf(
            ToolDeclaration(
                "get_weather",
                "get weather of city",
                listOf(
                    PrimitiveToolParameter(
                        ToolParameterType.STRING,
                        "city_name",
                        "city name",
                        true
                    )
                )
            )
        )
    )

    @Test
    fun chatCompletion() {
        runBlocking {
            val response = client.chatCompletion(request)

            println(gson.toJson(response))
        }
    }

    @Test
    fun chatCompletionAsync() {
        runBlocking {
            val response = client.chatCompletionAsync(request.copy(stream = true))
            response.collect {
                println(gson.toJson(it))
                val message = it.choices.first().message
                if (message.hasValidToolCall) {
                    println(">>>>>" + gson.toJson(message.toolCalls))
                }
            }
        }
    }
}