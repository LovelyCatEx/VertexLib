package com.lovelycatv.vertex.ai.workflow.graph

import com.lovelycatv.vertex.ai.workflow.graph.edge.GraphEdge
import com.lovelycatv.vertex.ai.workflow.graph.edge.GraphNodeParameterTransmissionEdge
import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractGraphNode
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeEntry
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeExit
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter
import kotlinx.coroutines.*
import java.util.*

/**
 * @author lovelycat
 * @since 2025-12-16 23:58
 * @version 1.0
 */
class WorkFlowGraph {
    private val graphNodeMap = mutableMapOf<String, AbstractGraphNode>()
    private val graphNodeTriggerEdges = mutableListOf<GraphEdge>()
    private val graphNodeParameterTransmissionEdges = mutableListOf<GraphNodeParameterTransmissionEdge>()
    private val graphNodeExecutionResult = mutableMapOf<String, Map<GraphNodeParameter, Any?>>()
    private val taskExecutors = mutableMapOf<String, WorkGraphCoroutineScope>()

    fun addNode(node: AbstractGraphNode) {
        this.graphNodeMap[node.nodeId] = node
    }

    fun removeNode(node: AbstractGraphNode) {
        this.graphNodeMap.remove(node.nodeId)

        this.graphNodeTriggerEdges.removeAll {
            it.from == node.nodeId || it.to == node.nodeId
        }

        this.graphNodeParameterTransmissionEdges.removeAll {
            it.from == node.nodeId ||it.to == node.nodeId
        }
    }

    fun addTriggerEdge(from: String, to: String) {
        this.assertNodeExists(from)
        this.assertNodeExists(to)

        this.graphNodeTriggerEdges.add(GraphEdge(from, to))
    }

    fun removeTriggerEdge(from: String, to: String) {
        this.graphNodeTriggerEdges.removeAll { it.from == from && it.to == to }
    }

    fun addParameterTransmissionEdge(from: String, to: String, fromParameter: String, toParameter: String) {
        this.assertNodeExists(from)
        this.assertNodeExists(to)

        this.graphNodeParameterTransmissionEdges.add(
            GraphNodeParameterTransmissionEdge(from, to, fromParameter, toParameter)
        )
    }

    fun removeParameterTransmissionEdge(from: String, to: String, fromParameter: String, toParameter: String) {
        this.graphNodeParameterTransmissionEdges.removeAll {
            it.from == from && it.to == to && it.fromParameterName == fromParameter && it.toParameterName == toParameter
        }
    }

    fun queryEdges(nodeId: String): GraphEdgeQueryResult {
        val node = this.assertNodeExists(nodeId)
        return this.queryEdges(node)
    }

    fun queryEdges(node: AbstractGraphNode): GraphEdgeQueryResult {
        return GraphEdgeQueryResult(
            node = node,
            triggerOrigins = graphNodeTriggerEdges
                .filter { it.to == node.nodeId }
                .map { it.from },
            triggerTargets = graphNodeTriggerEdges
                .filter {
                    it.from == node.nodeId
                }
                .groupBy {
                    it.groupId
                }
                .mapValues {
                    it.value.map {
                        it.to
                    }
                },
            parameterOrigins = node.inputs
                .associateWith { parameter ->
                    graphNodeParameterTransmissionEdges.find {
                        it.toParameterName == parameter.name
                    }
                }
                .mapKeys { it.key.name },
            parameterOutputs = node.inputs
                .associateWith { parameter ->
                    graphNodeParameterTransmissionEdges.filter {
                        it.fromParameterName == parameter.name
                    }
                }
                .mapKeys { it.key.name }
        )
    }

    fun getEntry(): GraphNodeEntry {
        return this.graphNodeMap.values.filterIsInstance<GraphNodeEntry>().run {
            if (this.size != 1) {
                throw IllegalStateException("There should be exactly one entry in graph")
            } else {
                this.first()
            }
        }
    }

    private suspend fun executeNode(scope: WorkGraphCoroutineScope, inputData: Map<GraphNodeParameter, Any?>, node: AbstractGraphNode) {
        // Execute
        val nodeExecutionResult = node.execute(inputData)

        // Save result into execution result map
        this.graphNodeExecutionResult[node.nodeId] = nodeExecutionResult

        if (node is GraphNodeExit) {
            scope.listener?.onTaskFinished(scope.taskId, nodeExecutionResult.mapKeys { it.key.name })
            return
        }

        val queryResult = this.queryEdges(node)

        val triggerGroup = node.determineTriggerGroup(inputData)
        val nextNodes = queryResult.triggerTargets
            // Find out group to be triggered
            .filterKeys { it in triggerGroup }
            // Collect target node ids list
            .values
            // Flatten
            .flatten()
            // Transform to actual node instance
            .map {
                this.assertNodeExists(it)
            }

        if (nextNodes.isEmpty()) {
            throw IllegalStateException("There are no nextNodes for this node ${node.nodeName}#${node.nodeId}")
        }

        nextNodes.forEach { nextNode ->
            scope.launch(Dispatchers.IO + CoroutineName("Node#${nextNode.nodeName}")) {
                executeNode(
                    scope,
                    // Result of global nodes
                    graphNodeExecutionResult
                        .mapKeys { assertNodeExists(it.key) }
                        .flatMap { (node, resultMap) ->
                            resultMap.mapKeys { it.key.copy(name = "${node.nodeName}.${it.key.name}") }.toList()
                        }.toMap() +
                    // Required by node
                    nextNode.inputs.associateWith { inputParameter ->
                        // Find edge which transmitting output to this input
                        val edge = queryEdges(nextNode).parameterOrigins[inputParameter.name]

                        if (edge != null) {
                            // Find whom produces the output and get all results of it
                            val result = graphNodeExecutionResult[edge.from]

                            if (result != null) {
                                // Find the output that this input requires
                                result[result.keys.find { it.name == edge.fromParameterName }]
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                    },
                    nextNode
                )
            }
        }
    }

    fun start(inputData: Map<GraphNodeParameter, Any?>, listener: WorkFlowGraphListener?): String {
        val taskId = UUID.randomUUID().toString()
        val scope = WorkGraphCoroutineScope(taskId, listener)

        this.taskExecutors[taskId] = scope

        val entry = this.getEntry()
        scope.launch(Dispatchers.IO + CoroutineName("Node#${entry.nodeName}")) {
            this@WorkFlowGraph.executeNode(scope, inputData, entry)
        }

        listener?.onTaskStarted(taskId)

        return taskId
    }

    fun stop(taskId: String) {
        this.taskExecutors[taskId]?.let {
            it.listener?.onTaskCancelled(taskId)
            it.cancel()
        }
    }

    suspend fun awaitTask(taskId: String) {
        if (!this.taskExecutors.containsKey(taskId)) {
            return
        }

        while (this.taskExecutors[taskId]!!.isActive) {
            delay(100)
        }
    }

    fun clear() {
        this.graphNodeMap.clear()
        this.graphNodeTriggerEdges.clear()
        this.graphNodeParameterTransmissionEdges.clear()
        this.graphNodeExecutionResult.clear()
    }

    @Suppress("UNCHECKED_CAST")
    private fun assertNodeExists(nodeId: String): AbstractGraphNode {
        val node = this.graphNodeMap[nodeId]
        return node ?: throw IllegalArgumentException("Node $nodeId not found in graph")
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: AbstractGraphNode> assertNodeExistsAsInstance(nodeId: String): T {
        val node = this.graphNodeMap[nodeId]
        return (node as? T?) ?: throw IllegalArgumentException("Node $nodeId not found in graph")
    }
}