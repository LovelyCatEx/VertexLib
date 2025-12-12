package com.lovelycatv.vertex.ai

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.message.ChatMessage
import com.lovelycatv.vertex.ai.request.ChatCompletionRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class VertexAIClientTest {
    private val aiClient = VertexAIClient(
        baseUrl = ModelProviderBaseUrl.DEEPSEEK,
        apiKey = System.getProperty("VertexAIClientTestApiKey")
    )

    private val weatherTool = ChatCompletionRequest.Tool(
        function = ChatCompletionRequest.ToolFunction(
            name = "get_weather",
            description = "Get weather of a location, the user should supply a location first.",
            parameters = ChatCompletionRequest.ToolFunction.ParametersDefinition(
                properties = mapOf(
                    "location" to ChatCompletionRequest.ToolFunction.Parameter(
                        type = ChatCompletionRequest.ToolFunction.Parameter.Type.STRING,
                        description = "The city and state, e.g. San Francisco, CA"
                    )
                ),
                required = listOf("location")
            )
        )
    )

    @Test
    fun chatCompletion() {
        runBlocking {
            val response = aiClient.chatCompletionBlocking(
                ChatCompletionRequest(
                    "deepseek-chat",
                    listOf(
                        ChatMessage(ChatMessageRole.USER, "hello")
                    ),
                    logprobs = true,
                    stream = false
                )
            )

            println(Gson().toJson(response))
        }
    }

    @Test
    fun chatCompletionStreaming() {
        runBlocking {
            val responseFlow = aiClient.chatCompletionStreaming(
                ChatCompletionRequest(
                    "deepseek-chat",
                    listOf(
                        ChatMessage(ChatMessageRole.USER, "hello")
                    ),
                    logprobs = true,
                    stream = true
                )
            )

            responseFlow.collect {
                println(Gson().toJson(it))
            }
        }
    }

    @Test
    fun toolCalls() {
        runBlocking {
            val response = aiClient.chatCompletionBlocking(
                ChatCompletionRequest(
                    "deepseek-chat",
                    listOf(
                        ChatMessage(ChatMessageRole.USER, "How's the weather in San Francisco, CA? ")
                    ),
                    logprobs = true,
                    stream = false,
                    tools = listOf(weatherTool)
                )
            )

            println(Gson().toJson(response))
        }
    }

    @Test
    fun toolCallsStreaming() {
        runBlocking {
            val response = aiClient.chatCompletionStreaming(
                ChatCompletionRequest(
                    "deepseek-chat",
                    listOf(
                        ChatMessage(ChatMessageRole.USER, "How's the weather in San Francisco, CA? ")
                    ),
                    logprobs = true,
                    stream = true,
                    tools = listOf(weatherTool)
                )
            )

            response.collect {
                println(Gson().toJson(it))
            }
        }
    }

    @Test
    fun listModels() {
        val models = runBlocking {
            aiClient.listModels()
        }

        println(models)
    }
}