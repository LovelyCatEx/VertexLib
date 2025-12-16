package com.lovelycatv.vertex.ai.openai

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.openai.message.ChatMessage
import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest
import com.lovelycatv.vertex.ai.openai.request.EmbeddingRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class VertexAIClientTest {
    private val aiClientDeepseek = VertexAIClient(
        baseUrl = ModelProviderBaseUrl.DEEPSEEK,
        apiKey = System.getProperty("VertexAIClientTestDeepSeekApiKey")
    )

    private val aiClientSiliconFlow = VertexAIClient(
        baseUrl = ModelProviderBaseUrl.SILICON_FLOW,
        apiKey = System.getProperty("VertexAIClientTestSiliconFlowApiKey")
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
            val response = aiClientDeepseek.chatCompletionBlocking(
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
            val responseFlow = aiClientDeepseek.chatCompletionStreaming(
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
            val response = aiClientDeepseek.chatCompletionBlocking(
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
            val response = aiClientDeepseek.chatCompletionStreaming(
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
    fun embeddings() {
        val result = runBlocking {
            aiClientSiliconFlow.embeddings(
                EmbeddingRequest("BAAI/bge-m3", listOf("a", "b"))
            )
        }

        println(result)
    }

    @Test
    fun listModels() {
        val models = runBlocking {
            aiClientDeepseek.listModels()
        }

        println(models)
    }
}