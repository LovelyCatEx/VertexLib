package com.lovelycatv.vertex.ai.openai.request

import com.google.gson.annotations.SerializedName
import com.lovelycatv.vertex.ai.openai.message.IChatMessage

/**
 * @author lovelycat
 * @since 2025-12-13 01:35
 * @version 1.0
 */
data class ChatCompletionRequest(
    val model: String,
    val messages: List<IChatMessage>,
    @SerializedName("max_tokens")
    val maxTokens: Int? = null,
    val stream: Boolean = false,
    @SerializedName("stream_options")
    val streamOptions: StreamOptions? = null,
    @SerializedName("frequency_penalty")
    val frequencyPenalty: Float? = null,
    @SerializedName("presence_penalty")
    val presencePenalty: Float? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val tools: List<Tool>? = null,
    val logprobs: Boolean? = null,
    val topLogprobs: Int? = null
) {
    fun validateParameters() {
        if (this.frequencyPenalty != null && this.frequencyPenalty !in (-2f..2f)) {
            throw IllegalArgumentException("frequency_penalty should be in -2 to 2f")
        }

        if (this.presencePenalty != null && this.presencePenalty !in (-2f..2f)) {
            throw IllegalArgumentException("presence_penalty should be in -2 to 2f")
        }

        if (this.temperature != null && this.temperature !in (0f..2f)) {
            throw IllegalArgumentException("temperature should be in 0f to 2f")
        }

        if (this.topP != null && this.topP !in (0f..1f)) {
            throw IllegalArgumentException("top_p should be in 0f to 1f")
        }

        if (this.topLogprobs != null && this.topLogprobs !in (0..20)) {
            throw IllegalArgumentException("top_logprobs should be in 0 to 20")
        }
    }

    data class StreamOptions(
        @SerializedName("include_usage")
        val includeUsage: Boolean = false
    )

    data class Tool(
        val type: String = "function",
        val function: ToolFunction
    )

    data class ToolFunction(
        val name: String,
        val description: String,
        val parameters: ParametersDefinition,
        val strict: Boolean = false
    ) {
        data class ParametersDefinition(
            val type: String = "object",
            val properties: Map<String, Property>,
            val required: List<String>
        )

        interface Property

        data class ConditionalParameter(
            val anyOf: List<Parameter>
        ) : Property

        data class Parameter(
            val type: Type,
            val description: String,
            val enum: List<String>? = null
        ) : Property {
            enum class Type(val typeName: String) {
                @SerializedName("string")
                STRING("string"),
                @SerializedName("number")
                NUMBER("number"),
                @SerializedName("integer")
                INTEGER("integer"),
                @SerializedName("array")
                ARRAY("array")
            }
        }
    }
}