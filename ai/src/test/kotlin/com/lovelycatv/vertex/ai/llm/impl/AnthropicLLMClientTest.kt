package com.lovelycatv.vertex.ai.llm.impl

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.llm.ChatRequest
import com.lovelycatv.vertex.ai.llm.StreamChatResponse
import com.lovelycatv.vertex.ai.llm.config.LLMClientConfig
import com.lovelycatv.vertex.ai.llm.config.LLMResponseConfigDefaults
import com.lovelycatv.vertex.ai.llm.message.AssistantChatMessage
import com.lovelycatv.vertex.ai.llm.message.SystemChatMessage
import com.lovelycatv.vertex.ai.llm.message.ToolCall
import com.lovelycatv.vertex.ai.llm.message.ToolChatMessage
import com.lovelycatv.vertex.ai.llm.message.UserChatMessage
import com.lovelycatv.vertex.ai.llm.tool.ToolDeclaration
import com.lovelycatv.vertex.ai.llm.tool.parameter.PrimitiveToolParameter
import com.lovelycatv.vertex.ai.llm.tool.parameter.ToolParameterType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test

class AnthropicLLMClientTest {
    private val gson = Gson()

    private val config = LLMClientConfig(
        baseUrl = "https://api.deepseek.com/anthropic/v1",
        apiKey = "",
        chatCompletionPath = "messages",
        llmResponseConfig = LLMResponseConfigDefaults.ANTHROPIC,
    )

    private val client = AnthropicLLMClient(
        config
    )

    private val tools = listOf(
        ToolDeclaration(
            "get_weather",
            "get weather of city",
            listOf(
                PrimitiveToolParameter(
                    ToolParameterType.STRING,
                    "city",
                    "city name",
                    true
                )
            )
        )
    )

    @Test
    fun transformRequestBody() {
        val request = ChatRequest(
            model = "deepseek-v4-pro",
            messages = listOf(
                SystemChatMessage("you are a helpful assistant"),
                UserChatMessage("how's the weather today in beijing, shanghai and suzhou"),
                AssistantChatMessage(
                    content = null,
                    reasoningContent = null,
                    toolCalls = listOf(
                        ToolCall(0, "call_1", "get_weather", mapOf("city" to "beijing"), """{"city":"beijing"}""", false)
                    ),
                    stopReason = null,
                    stopReasonString = null,
                ),
                // Two adjacent tool results have to collapse into one `user` turn — the API
                // rejects consecutive same-role messages.
                ToolChatMessage("call_1", "sunny, 26°C"),
                ToolChatMessage("call_2", "cloudy, 24°C"),
            ),
            stream = false,
            tools = tools,
            maxCompletionTokens = 1000,
        )

        val body = gson.fromJson(client.transformRequestBody(request), Map::class.java)

        assertEquals("deepseek-v4-pro", body["model"])
        assertEquals(1000.0, body["max_tokens"])
        assertEquals(false, body["stream"])
        assertEquals("you are a helpful assistant", body["system"])

        @Suppress("UNCHECKED_CAST")
        val messages = body["messages"] as List<Map<String, Any?>>
        assertEquals(3, messages.size)
        assertEquals("user", messages[0]["role"])
        assertEquals("assistant", messages[1]["role"])
        assertEquals("user", messages[2]["role"])

        @Suppress("UNCHECKED_CAST")
        val assistantBlocks = messages[1]["content"] as List<Map<String, Any?>>
        assertEquals("tool_use", assistantBlocks.single()["type"])
        assertEquals("get_weather", assistantBlocks.single()["name"])

        @Suppress("UNCHECKED_CAST")
        val toolResultBlocks = messages[2]["content"] as List<Map<String, Any?>>
        assertEquals(2, toolResultBlocks.size)
        assertEquals(listOf("call_1", "call_2"), toolResultBlocks.map { it["tool_use_id"] })

        @Suppress("UNCHECKED_CAST")
        val tool = (body["tools"] as List<Map<String, Any?>>).single()
        assertEquals("get_weather", tool["name"])
        assertEquals(true, tool["strict"])

        @Suppress("UNCHECKED_CAST")
        val schema = tool["input_schema"] as Map<String, Any?>
        assertEquals("object", schema["type"])
        assertEquals(false, schema["additionalProperties"])
        assertEquals(listOf("city"), schema["required"])
    }

    @Test
    fun chatCompletion() {
        runBlocking {
            val response = client.chatCompletion(request(stream = false))

            println(gson.toJson(response))

            assertTrue(response.success)
            assertTrue(response.id.isNotEmpty())
            assertEquals(1, response.choices.size, "every response carries exactly one choice")
            assertTrue(response.usage.promptTokens > 0, "prompt tokens should be reported")
            assertTrue(response.usage.completionTokens > 0, "completion tokens should be reported")
        }
    }

    @Test
    fun chatCompletionWithToolCall() {
        runBlocking {
            val response = client.chatCompletion(request(stream = false))
            val message = response.choices.first().message

            println(gson.toJson(message))

            if (message.hasValidToolCall) {
                val toolCall = message.toolCalls!!.first()
                assertEquals("get_weather", toolCall.toolName)
                assertTrue(toolCall.id.isNotEmpty(), "tool_use blocks carry an id to answer with")
                assertTrue(toolCall.arguments.containsKey("city"))
            }
        }
    }

    @Test
    fun chatCompletionAsync() {
        runBlocking {
            val texts = StringBuilder()
            val toolCalls = mutableListOf<ToolCall>()
            var promptTokens = 0
            var completionTokens = 0
            var finished = false

            client.chatCompletionAsync(request(stream = true)).collect { streamResponse: StreamChatResponse ->
                assertEquals(1, streamResponse.choices.size, "every event carries exactly one choice")
                println(gson.toJson(streamResponse))

                val message = streamResponse.choices.first().message
                texts.append(message.content.orEmpty())

                if (message.hasValidToolCall) {
                    println(">>>>" + gson.toJson(message.toolCalls))
                    toolCalls += message.toolCalls!!.filter { !it.streaming }
                }

                // `message_start` nests these under `message`, `message_delta` reports the
                // completion total at the top level.
                promptTokens += streamResponse.usage.promptTokens
                completionTokens += streamResponse.usage.completionTokens
                finished = finished || streamResponse.finished
            }

            println("text: $texts")
            println("toolCalls: ${gson.toJson(toolCalls)}")

            assertTrue(finished, "the stream should end on message_delta / message_stop")
            assertTrue(promptTokens > 0, "message_start should report the prompt tokens")
            assertTrue(completionTokens > 0, "message_delta should report the completion tokens")
            assertTrue(texts.isNotEmpty() || toolCalls.isNotEmpty(), "the stream produced no content")
        }
    }

    private fun request(stream: Boolean) = ChatRequest(
        model = System.getenv("ANTHROPIC_MODEL") ?: "deepseek-flash",
        messages = listOf(UserChatMessage("how's the weather today in beijing, shanghai and suzhou")),
        stream = stream,
        tools = tools,
        maxCompletionTokens = 1000,
    )
}
