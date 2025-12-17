package com.lovelycatv.vertex.workflow.graph.node

import java.util.*
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-12-17 00:02
 * @version 1.0
 */
class GraphNodeExit<R: Any>(
    nodeId: String = UUID.randomUUID().toString(),
    nodeName: String,
    val outputValueType: KClass<R>,
    val strict: Boolean = false
) : BaseGraphNode(
    GraphNodeType.EXIT,
    nodeId,
    nodeName,
    listOf(
        GraphNodeParameter(outputValueType, OUTPUT)
    ),
    listOf(
        GraphNodeParameter(outputValueType, OUTPUT)
    )
) {
    companion object {
        const val OUTPUT = "output"
    }

    override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
        return if (!strict) {
            inputData
        } else {
            outputs.associateWith {
                inputData[it]
            }
        }
    }

    override fun serialize(): Map<String, Any> {
        return super.serialize() + mapOf(
            "outputValueType" to outputValueType.java.canonicalName,
            "strict" to strict.toString()
        )
    }
}