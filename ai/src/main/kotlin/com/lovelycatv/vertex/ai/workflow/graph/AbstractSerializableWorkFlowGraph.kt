package com.lovelycatv.vertex.ai.workflow.graph

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.workflow.graph.edge.GraphNodeParameterTransmissionEdge
import com.lovelycatv.vertex.ai.workflow.graph.edge.GraphTriggerEdge
import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractSerializableGraphNode
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeType
import com.lovelycatv.vertex.ai.workflow.graph.node.IGraphNodeType
import com.lovelycatv.vertex.ai.workflow.graph.serializer.*

/**
 * @author lovelycat
 * @since 2025-12-16 23:58
 * @version 1.0
 */
abstract class AbstractSerializableWorkFlowGraph<V: AbstractSerializableGraphNode, R: Any>(
    graphName: String
) : AbstractWorkFlowGraph<V, R>(graphName) {
    private val deserializerMap: MutableMap<String, GraphNodeDeserializer<out AbstractSerializableGraphNode>> = mutableMapOf(
        GraphNodeType.ENTRY.getTypeName() to GraphNodeEntryDeserializer(),
        GraphNodeType.EXIT.getTypeName() to GraphNodeExitDeserializer(),
        GraphNodeType.IF.getTypeName() to GraphNodeIfDeserializer(),
        GraphNodeType.ADD.getTypeName() to GraphNodeAddDeserializer(),
        GraphNodeType.SUB.getTypeName() to GraphNodeSubDeserializer(),
        GraphNodeType.MUL.getTypeName() to GraphNodeMulDeserializer(),
        GraphNodeType.DIV.getTypeName() to GraphNodeDivDeserializer(),
        GraphNodeType.NUMBER_COMPARATOR.getTypeName() to GraphNodeNumberComparatorDeserializer(),
    )

    fun registerDeserializer(nodeType: IGraphNodeType, deserializer: GraphNodeDeserializer<*>) {
        this.deserializerMap[nodeType.getTypeName()] = deserializer
    }

    @Suppress("UNCHECKED_CAST")
    fun getDeserializer(nodeType: String): GraphNodeDeserializer<V> {
        return (this.deserializerMap[nodeType]
            ?: throw IllegalArgumentException("Unknown deserializer of node type $nodeType")
        ) as GraphNodeDeserializer<V>
    }

    fun serialize(gson: Gson = Gson()): String {
        return gson.toJson(Serialization(
            this.graphNodeMap.mapValues { it.value.serialize() },
            this.graphNodeTriggerEdges,
            this.graphNodeParameterTransmissionEdges
        ))
    }

    fun loadFromSerialized(serialized: String, gson: Gson = Gson()) {
        this.clear()

        val serialization = gson.fromJson(serialized, Serialization::class.java)

        serialization.graphNodeMap.values.forEach {
            this.addNode(this.getDeserializer(it["nodeType"] as String).deserialize(it))
        }

        serialization.graphNodeTriggerEdges.forEach {
            this.addTriggerEdge(it)
        }

        serialization.graphNodeParameterTransmissionEdges.forEach {
            this.addParameterTransmissionEdge(it)
        }
    }

    data class Serialization(
        val graphNodeMap: Map<String, Map<String, Any>>,
        val graphNodeTriggerEdges: List<GraphTriggerEdge>,
        val graphNodeParameterTransmissionEdges: List<GraphNodeParameterTransmissionEdge>
    )
}