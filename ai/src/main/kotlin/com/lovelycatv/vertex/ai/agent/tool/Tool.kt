package com.lovelycatv.vertex.ai.agent.tool

/**
 * Marks a function as a callable tool, or a value parameter as a tool parameter.
 *
 * - On a **function**: the function becomes a tool. [name] overrides the tool name
 *   (defaults to the function name) and [description] describes the tool for the model.
 * - On a **value parameter**: [name] overrides the parameter name (defaults to the
 *   parameter name), [description] describes the parameter, and [enum] restricts the
 *   accepted values.
 *
 * Only parameter types that map to [com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest.ToolFunction.Parameter.Type]
 * are supported.
 *
 * @author lovelycat
 * @since 2026-07-14
 * @version 1.0
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Tool(
    val name: String = "",
    val description: String = "",
    val enum: Array<String> = []
)
