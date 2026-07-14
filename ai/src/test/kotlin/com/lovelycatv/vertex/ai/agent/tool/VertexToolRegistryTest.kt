package com.lovelycatv.vertex.ai.agent.tool

import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class VertexToolRegistryTest {
    /**
     * A sample provider: every @Tool-annotated function becomes a callable tool.
     */
    class WeatherProvider {
        @Tool(description = "Get weather of a location, the user should supply a location first.")
        fun getWeather(
            @Tool(description = "The city and state, e.g. San Francisco, CA")
            location: String,
            @Tool(description = "Temperature unit", enum = ["celsius", "fahrenheit"])
            unit: String = "celsius"
        ): String {
            return "Weather in $location: 20 ($unit)"
        }

        @Tool(name = "add_numbers", description = "Add two integers")
        fun add(a: Int, b: Int): Int = a + b

        fun notATool(): String = "ignored"
    }

    @Test
    fun convertAndInvoke() {
        val registry = VertexToolRegistry(listOf(WeatherProvider()))

        val tools = registry.getTools()
        assertEquals(2, tools.size)

        val weather = tools.first { it.function.name == "getWeather" }.function
        assertEquals(
            "Get weather of a location, the user should supply a location first.",
            weather.description
        )
        // location is required (no default, non-null); unit has a default so it is optional
        assertTrue(weather.parameters.required.contains("location"))
        assertTrue(!weather.parameters.required.contains("unit"))

        val locationParam = weather.parameters.properties["location"] as ChatCompletionRequest.ToolFunction.Parameter
        assertEquals(ChatCompletionRequest.ToolFunction.Parameter.Type.STRING, locationParam.type)

        val unitParam = weather.parameters.properties["unit"] as ChatCompletionRequest.ToolFunction.Parameter
        assertEquals(listOf("celsius", "fahrenheit"), unitParam.enum)

        val addTool = tools.first { it.function.name == "add_numbers" }.function
        val aParam = addTool.parameters.properties["a"] as ChatCompletionRequest.ToolFunction.Parameter
        assertEquals(ChatCompletionRequest.ToolFunction.Parameter.Type.INTEGER, aParam.type)

        runBlocking {
            val weatherResult = registry.invoke(
                "getWeather",
                mapOf("location" to "San Francisco, CA", "unit" to "fahrenheit")
            )
            assertEquals("Weather in San Francisco, CA: 20 (fahrenheit)", weatherResult)

            // default parameter path: unit omitted
            val defaultUnit = registry.invoke("getWeather", mapOf("location" to "Tokyo"))
            assertEquals("Weather in Tokyo: 20 (celsius)", defaultUnit)

            // numbers arrive from JSON as Double; coercion should hand Int to the function
            val sum = registry.invoke("add_numbers", mapOf("a" to 2.0, "b" to 3.0))
            assertEquals(5, sum)
        }
    }

    @Test
    fun unknownToolThrows() {
        val registry = VertexToolRegistry()
        assertTrue(!registry.contains("nope"))

        val error = runCatching {
            runBlocking { registry.invoke("nope", emptyMap()) }
        }.exceptionOrNull()
        assertNotNull(error)
        assertTrue(error is IllegalArgumentException)
    }
}
