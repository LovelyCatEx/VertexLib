package com.lovelycatv.vertex.ai.llm.impl

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.llm.ChatRequest
import com.lovelycatv.vertex.ai.llm.ReasoningEffort
import com.lovelycatv.vertex.ai.llm.config.LLMClientConfig
import com.lovelycatv.vertex.ai.llm.message.UserChatMessage
import com.lovelycatv.vertex.ai.llm.tool.ToolDeclaration
import com.lovelycatv.vertex.ai.llm.tool.parameter.PrimitiveToolParameter
import com.lovelycatv.vertex.ai.llm.tool.parameter.ToolParameter
import com.lovelycatv.vertex.ai.llm.tool.parameter.ToolParameterType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test

class OpenAiLLMClientTest {
    private val gson = Gson()

    /** The live tests below need a real key; without one the requests simply come back rejected. */
    private val apiKey: String = System.getenv("DEEPSEEK_API_KEY") ?: ""

    private val config = LLMClientConfig(
        baseUrl = "https://api.deepseek.com/v1",
        apiKey = apiKey
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
    fun resolveModelsPage() {
        val body = """
            {"object":"list","data":[
              {"id":"deepseek-flash","object":"model","owned_by":"deepseek"},
              {"id":"deepseek-v4-pro","object":"model","created":1753315200,"owned_by":"deepseek"}
            ]}
        """.trimIndent()

        val page = client.resolveModelsPage(body)

        assertEquals(listOf("deepseek-flash", "deepseek-v4-pro"), page.models.map { it.id })
        assertEquals("deepseek", page.models[0].ownedBy)
        assertNull(page.models[0].createdAt, "not every compatible endpoint reports a creation time")
        assertEquals(1753315200L, page.models[1].createdAt)
        assertNull(page.nextCursor, "the OpenAI-compatible list is not paginated")
    }

    @Test
    fun transformRequestBodyMapsReasoningEffort() {
        fun reasoningEffortFor(effort: ReasoningEffort): Any? = gson.fromJson(
            client.transformRequestBody(request.copy(reasoningEffort = effort)),
            Map::class.java
        )["reasoning_effort"]

        // "none" is what actually turns reasoning off; omitting the field would leave it on.
        assertEquals("none", reasoningEffortFor(ReasoningEffort.DISABLED))
        assertNull(reasoningEffortFor(ReasoningEffort.AUTO), "AUTO leaves the choice to the provider")
        assertEquals("minimal", reasoningEffortFor(ReasoningEffort.MINIMAL))
        assertEquals("low", reasoningEffortFor(ReasoningEffort.LOW))
        assertEquals("medium", reasoningEffortFor(ReasoningEffort.MEDIUM))
        assertEquals("high", reasoningEffortFor(ReasoningEffort.HIGH))
        assertEquals("xhigh", reasoningEffortFor(ReasoningEffort.EXTRA_HIGH))
        assertEquals("xhigh", reasoningEffortFor(ReasoningEffort.MAX))
    }

    @Test
    fun listModels() {
        assumeTrue(apiKey.isNotEmpty(), "DEEPSEEK_API_KEY not set")

        runBlocking {
            val models = client.listModels()

            println(gson.toJson(models))

            assertTrue(models.isNotEmpty())
            assertTrue(models.any { "deepseek" in it.id })
        }
    }

    @Test
    fun chatCompletion() {
        assumeTrue(apiKey.isNotEmpty(), "DEEPSEEK_API_KEY not set")

        runBlocking {
            val response = client.chatCompletion(request)

            println(gson.toJson(response))

            assertTrue(response.success)
            assertTrue(response.choices.first().message.content?.isNotEmpty() == true)
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