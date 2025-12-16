package com.lovelycatv.vertex.ai.workflow.graph.node

import com.lovelycatv.vertex.ai.workflow.WorkFlowGraphConstants.DEFAULT_EDGE_GROUP
import java.util.*

/**
 * @author lovelycat
 * @since 2025-12-16 23:58
 * @version 1.0
 */
abstract class AbstractGraphNode(
    val nodeType: IGraphNodeType,
    val nodeId: String = UUID.randomUUID().toString(),
    val nodeName: String,
    val inputs: List<GraphNodeParameter>,
    val outputs: List<GraphNodeParameter>
) {
    abstract suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?>

    open fun determineTriggerGroup(inputData: Map<GraphNodeParameter, Any?>): List<String> {
        return listOf(DEFAULT_EDGE_GROUP)
    }

    protected fun resolveParameters(inputData: Map<GraphNodeParameter, Any?>, text: String): String {
        val regex = Regex("\\{\\{(.*?)}}")

        return regex.replace(text) { match ->
            val key = match.groupValues[1]
            resolveParameterReference(inputData, key).toString()
        }
    }

    protected fun resolveParameterReference(inputData: Map<GraphNodeParameter, Any?>, ref: String): Any? {
        val t = inputData.mapKeys { it.key.name }
        return if (!ref.contains(".")) {
            if (t.contains(ref)) {
                t[ref]
            } else {
                throw IllegalArgumentException("Parameter $ref not found")
            }
        } else {
            val paths = ref.split(".")
            var currentMap: Map<String, Any?> = t
            paths.forEachIndexed { index, path ->
                if (index != paths.size - 1) {
                    @Suppress("UNCHECKED_cAST")
                    currentMap = currentMap[path] as Map<String, Any?>
                } else {
                    return currentMap[path]
                }
            }

            throw IllegalArgumentException("Parameter $ref not found")
        }
    }
}