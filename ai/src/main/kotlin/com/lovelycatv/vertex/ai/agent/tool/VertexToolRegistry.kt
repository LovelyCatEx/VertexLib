package com.lovelycatv.vertex.ai.agent.tool

import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest

/**
 * Holds tools discovered from providers and dispatches calls to them by name.
 *
 * Register a provider with [register] (or pass providers to the constructor); the registry
 * scans each provider's [Tool]-annotated functions via [ToolConverter]. At runtime, given a
 * tool name and a parameter map (as produced by the model), [invoke] locates the matching
 * function and calls it.
 *
 * @author lovelycat
 * @since 2026-07-14
 * @version 1.0
 */
class VertexToolRegistry(providers: List<Any> = emptyList()) {
    private val registeredTools = mutableMapOf<String, RegisteredTool>()

    init {
        providers.forEach { register(it) }
    }

    /**
     * Scans [provider] for [Tool]-annotated functions and registers each as a callable tool.
     * Throws if a tool name collides with one already registered.
     */
    fun register(provider: Any): VertexToolRegistry {
        ToolConverter.convert(provider).forEach { registered ->
            require(!registeredTools.containsKey(registered.name)) {
                "A tool named '${registered.name}' is already registered"
            }
            registeredTools[registered.name] = registered
        }
        return this
    }

    /**
     * The OpenAI tool definitions for every registered tool, ready to be placed in a
     * [ChatCompletionRequest].
     */
    fun getTools(): List<ChatCompletionRequest.Tool> {
        return registeredTools.values.map { it.tool }
    }

    /**
     * Whether a tool with the given [name] is registered.
     */
    fun contains(name: String): Boolean = registeredTools.containsKey(name)

    /**
     * Invokes the tool named [name] with the given [arguments] (keyed by parameter name).
     *
     * @throws IllegalArgumentException if no tool with that name is registered.
     */
    suspend fun invoke(name: String, arguments: Map<String, Any?>): Any? {
        val registered = registeredTools[name]
            ?: throw IllegalArgumentException("No tool named '$name' is registered")
        return registered.invoke(arguments)
    }
}
