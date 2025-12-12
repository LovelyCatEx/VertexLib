package com.lovelycatv.vertex.ai

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.request.ChatCompletionRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class VertexAIClientTest {
    private val agent = VertexAIClient(
        baseUrl = ModelProviderBaseUrl.DEEPSEEK,
        apiKey = System.getProperty("VertexAIClientTestApiKey")
    )

    @Test
    fun chatCompletion() {
        runBlocking {
            val response = agent.chatCompletionBlocking(
                ChatCompletionRequest(
                    "deepseek-chat",
                    listOf(
                        ChatMessage(ChatMessageRole.USER, "hello")
                    ),
                    logprobs = true,
                    stream = true
                )
            )

            println(Gson().toJson(response))
        }
    }

    @Test
    fun chatCompletionStreaming() {
        runBlocking {
            val responseFlow = agent.chatCompletionStreaming(
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
    fun listModels() {
        val models = runBlocking {
            agent.listModels()
        }

        println(models)
    }
}