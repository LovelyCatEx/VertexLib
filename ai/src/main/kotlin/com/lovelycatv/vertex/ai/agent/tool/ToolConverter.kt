package com.lovelycatv.vertex.ai.agent.tool

import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

/**
 * Converts a provider object into a list of [RegisteredTool]s by scanning its member
 * functions for the [Tool] annotation.
 *
 * Only parameter types that map to [ChatCompletionRequest.ToolFunction.Parameter.Type]
 * are supported: [String]/[Char] (string), [Int]/[Long]/[Short]/[Byte] (integer),
 * [Double]/[Float] (number), [Collection] (array), and any other class (object).
 *
 * @author lovelycat
 * @since 2026-07-14
 * @version 1.0
 */
object ToolConverter {
    /**
     * Scans [provider] for functions annotated with [Tool] and converts each into a
     * [RegisteredTool].
     */
    fun convert(provider: Any): List<RegisteredTool> {
        return provider::class.memberFunctions
            .filter { it.findAnnotation<Tool>() != null }
            .map { convertFunction(provider, it) }
    }

    private fun convertFunction(provider: Any, function: KFunction<*>): RegisteredTool {
        val toolAnnotation = function.findAnnotation<Tool>()!!
        val toolName = toolAnnotation.name.ifBlank { function.name }

        val properties = mutableMapOf<String, ChatCompletionRequest.ToolFunction.Property>()
        val required = mutableListOf<String>()
        val parameterNames = mutableListOf<String>()

        function.parameters
            .filter { it.kind == KParameter.Kind.VALUE }
            .forEach { parameter ->
                val paramAnnotation = parameter.findAnnotation<Tool>()
                val paramName = paramAnnotation?.name?.ifBlank { null }
                    ?: parameter.name
                    ?: throw IllegalArgumentException(
                        "Parameter of tool '$toolName' has no name; compile with parameter names or set @Tool(name = ...)"
                    )

                parameterNames += paramName
                properties[paramName] = ChatCompletionRequest.ToolFunction.Parameter(
                    type = resolveType(parameter),
                    description = paramAnnotation?.description ?: "",
                    enum = paramAnnotation?.enum?.takeIf { it.isNotEmpty() }?.toList()
                )

                if (!parameter.isOptional && !parameter.type.isMarkedNullable) {
                    required += paramName
                }
            }

        val tool = ChatCompletionRequest.Tool(
            function = ChatCompletionRequest.ToolFunction(
                name = toolName,
                description = toolAnnotation.description,
                parameters = ChatCompletionRequest.ToolFunction.ParametersDefinition(
                    properties = properties,
                    required = required
                )
            )
        )

        return RegisteredTool(
            name = toolName,
            tool = tool,
            provider = provider,
            function = function,
            parameterNames = parameterNames
        )
    }

    private fun resolveType(parameter: KParameter): ChatCompletionRequest.ToolFunction.Parameter.Type {
        val classifier = parameter.type.classifier as? KClass<*>
            ?: throw IllegalArgumentException("Unsupported parameter type: ${parameter.type}")

        return when (classifier) {
            String::class, Char::class -> ChatCompletionRequest.ToolFunction.Parameter.Type.STRING
            Boolean::class -> ChatCompletionRequest.ToolFunction.Parameter.Type.BOOLEAN
            Int::class, Long::class, Short::class, Byte::class ->
                ChatCompletionRequest.ToolFunction.Parameter.Type.INTEGER
            Double::class, Float::class -> ChatCompletionRequest.ToolFunction.Parameter.Type.NUMBER
            else -> when {
                classifier.isSubclassOf(Collection::class) -> ChatCompletionRequest.ToolFunction.Parameter.Type.ARRAY
                else -> ChatCompletionRequest.ToolFunction.Parameter.Type.OBJECT
            }
        }
    }

    private fun KClass<*>.isSubclassOf(base: KClass<*>): Boolean {
        return base.java.isAssignableFrom(this.java)
    }
}
