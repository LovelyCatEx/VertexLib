package com.lovelycatv.vertex.ai.agent.tool

import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.valueParameters

/**
 * A tool discovered from a provider: its OpenAI tool definition plus a way to invoke it.
 *
 * @author lovelycat
 * @since 2026-07-14
 * @version 1.0
 */
class RegisteredTool(
    val name: String,
    val tool: ChatCompletionRequest.Tool,
    private val provider: Any,
    private val function: KFunction<*>,
    private val parameterNames: List<String>
) {
    /**
     * Invokes the underlying function with the given argument map (keyed by the tool
     * parameter names). Missing arguments are passed as null; the function's own
     * nullability/defaults decide whether that is acceptable.
     */
    suspend fun invoke(arguments: Map<String, Any?>): Any? {
        val args = mutableMapOf<KParameter, Any?>(function.instanceParameter!! to provider)
        function.valueParameters.forEachIndexed { index, kParameter ->
            val name = parameterNames[index]
            if (arguments.containsKey(name)) {
                args[kParameter] = coerce(arguments[name], kParameter)
            } else if (!kParameter.isOptional) {
                args[kParameter] = null
            }
        }
        return function.callSuspendBy(args)
    }

    /**
     * Coerces a raw argument (as decoded from JSON, where every number is a [Double]) into the
     * concrete Kotlin type the parameter expects. Non-numeric values are passed through as-is.
     */
    private fun coerce(value: Any?, parameter: KParameter): Any? {
        if (value == null) return null
        val classifier = parameter.type.classifier as? KClass<*> ?: return value
        if (value is Number) {
            return when (classifier) {
                Int::class -> value.toInt()
                Long::class -> value.toLong()
                Short::class -> value.toShort()
                Byte::class -> value.toByte()
                Double::class -> value.toDouble()
                Float::class -> value.toFloat()
                else -> value
            }
        }
        return value
    }
}
