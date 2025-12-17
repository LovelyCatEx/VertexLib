package com.lovelycatv.vertex.ai.workflow.graph

import com.lovelycatv.vertex.ai.workflow.WorkFlowGraphConstants.DEFAULT_EDGE_GROUP
import com.lovelycatv.vertex.ai.workflow.graph.edge.GraphNodeParameterTransmissionEdge
import com.lovelycatv.vertex.ai.workflow.graph.edge.GraphTriggerEdge
import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractGraphNode
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeEntry
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeExit
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter
import kotlinx.coroutines.*
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * @author lovelycat
 * @since 2025-12-16 23:58
 * @version 1.0
 */
abstract class AbstractWorkFlowGraph<V: AbstractGraphNode, R: Any>(val graphName: String) {
    protected val graphNodeMap: MutableMap<String, V> = mutableMapOf()
    protected val graphNodeTriggerEdges = mutableListOf<GraphTriggerEdge>()
    protected val graphNodeParameterTransmissionEdges = mutableListOf<GraphNodeParameterTransmissionEdge>()
    protected val graphNodeExecutionResult = mutableMapOf<String, Map<GraphNodeParameter, Any?>>()
    protected val taskExecutors = mutableMapOf<String, WorkGraphCoroutineScope<V, R>>()

    fun addNode(node: V) {
        this.graphNodeMap[node.nodeId] = node
    }

    fun removeNode(node: V) {
        this.graphNodeMap.remove(node.nodeId)

        this.graphNodeTriggerEdges.removeAll {
            it.from == node.nodeId || it.to == node.nodeId
        }

        this.graphNodeParameterTransmissionEdges.removeAll {
            it.from == node.nodeId ||it.to == node.nodeId
        }
    }

    fun addTriggerEdge(edge: GraphTriggerEdge) {
        this.addTriggerEdge(edge.from, edge.to, edge.groupId)
    }

    fun addTriggerEdge(from: String, to: String, groupId: String = DEFAULT_EDGE_GROUP) {
        this.getNodeById(from)
        this.getNodeById(to)

        this.graphNodeTriggerEdges.add(GraphTriggerEdge(from, to, groupId))
    }

    fun removeTriggerEdge(from: String, to: String, groupId: String = DEFAULT_EDGE_GROUP) {
        this.graphNodeTriggerEdges.removeAll { it.from == from && it.to == to && it.groupId == groupId }
    }

    fun addParameterTransmissionEdge(edge: GraphNodeParameterTransmissionEdge) {
        this.addParameterTransmissionEdge(
            edge.from,
            edge.to,
            edge.fromParameterName,
            edge.toParameterName
        )
    }

    fun addParameterTransmissionEdge(from: String, to: String, fromParameter: GraphNodeParameter, toParameter: GraphNodeParameter) {
        this.addParameterTransmissionEdge(from, to, fromParameter.name, toParameter.name)
    }

    fun addParameterTransmissionEdge(from: String, to: String, fromParameter: String, toParameter: String) {
        val fromNode = this.getNodeById(from)
        val toNode = this.getNodeById(to)

        // Check parameter type
        val typeFrom = fromNode.outputs.find { it.name == fromParameter }?.type
            ?: throw IllegalArgumentException("$fromParameter is undefined in node outputs of $from")
        val typeTo = toNode.inputs.find { it.name == toParameter }?.type
            ?: throw IllegalArgumentException("$toParameter is undefined in node inputs of $to")

        if (!typeFrom.isSubclassOf(typeTo)) {
            throw IllegalArgumentException("Parameter $fromParameter (${typeFrom.qualifiedName}) in $${fromNode.displayName()} " +
                    "cannot be assigned to $toParameter (${typeTo.qualifiedName}) in ${toNode.displayName()}")
        }

        // Check whether the connection is valid
        val assignment = queryEdges(toNode).parameterOrigins[toParameter]
        if (assignment != null) {
            throw IllegalStateException("There are already exists an assignment " +
                    "(from ${assignment.fromParameterName} in node ${assignment.from}) " +
                    "of parameter $toParameter in node ${toNode.displayName()}")
        }

        this.graphNodeParameterTransmissionEdges.add(
            GraphNodeParameterTransmissionEdge(
                from = from,
                to = to, 
                fromParameterName = fromParameter,
                toParameterName = toParameter
            )
        )
    }

    fun removeParameterTransmissionEdge(from: String, to: String, fromParameter: GraphNodeParameter, toParameter: GraphNodeParameter) {
        this.removeParameterTransmissionEdge(from, to, fromParameter.name, toParameter.name)
    }

    fun removeParameterTransmissionEdge(from: String, to: String, fromParameter: String, toParameter: String) {
        this.graphNodeParameterTransmissionEdges.removeAll {
            it.from == from && it.to == to && it.fromParameterName == fromParameter && it.toParameterName == toParameter
        }
    }

    fun queryEdges(nodeId: String): GraphEdgeQueryResult {
        val node = this.getNodeById(nodeId)
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
                        it.to == node.nodeId && it.toParameterName == parameter.name
                    }
                }
                .mapKeys { it.key.name },
            parameterOutputs = node.inputs
                .associateWith { parameter ->
                    graphNodeParameterTransmissionEdges.filter {
                        it.from == node.nodeId && it.fromParameterName == parameter.name
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

    @Suppress("UNCHECKED_CAST")
    fun getOutputType(): KClass<R> {
        return this.graphNodeMap.values.filterIsInstance<GraphNodeExit<*>>().run {
            if (this.size != 1) {
                throw IllegalStateException("There should be exactly one output type in graph, " +
                        "current: ${this.joinToString { it.outputValueType.qualifiedName ?: it.outputValueType.java.canonicalName }}")
            } else {
                this.first().outputValueType
            } as KClass<R>
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected suspend fun executeNode(scope: WorkGraphCoroutineScope<V, R>, inputData: Map<GraphNodeParameter, Any?>, node: AbstractGraphNode) {
        // Execute
        val nodeExecutionResult = node.execute(inputData)

        // Save result into execution result map
        this.graphNodeExecutionResult[node.nodeId] = nodeExecutionResult

        if (node is GraphNodeExit<*>) {
            scope.listener?.onTaskFinished(
                scope.taskId,
                nodeExecutionResult.values.first() as R?
            )
            return
        }

        val queryResult = this.queryEdges(node)

        val triggerGroup = node.determineTriggerGroups(inputData)
        val nextNodes = queryResult.triggerTargets
            // Find out group to be triggered
            .filterKeys { it in triggerGroup }
            // Collect target node ids list
            .values
            // Flatten
            .flatten()
            // Transform to actual node instance
            .map {
                this.getNodeById(it)
            }

        if (nextNodes.isEmpty()) {
            throw IllegalStateException("There are no nextNodes for this node ${node.nodeName}#${node.nodeId}")
        }

        nextNodes.forEach { nextNode ->
            scope.launchNodeExecution(nextNode) {
                executeNode(
                    scope,
                    // Result of global nodes, may be used in resolve parameters
                    graphNodeExecutionResult
                        .mapKeys { getNodeById(it.key) }
                        .flatMap { (node, resultMap) ->
                            resultMap.mapKeys { it.key.copy(name = "${node.nodeName}.${it.key.name}") }.toList()
                        }.toMap() +
                            // Required by node
                            nextNode.inputs.associateWith { inputParameter ->
                                // Find edge which transmitting output to this input
                                val edges = queryEdges(nextNode)

                                val edge = edges.parameterOrigins[inputParameter.name]

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

    fun start(inputData: Map<GraphNodeParameter, Any?>, listener: WorkFlowGraphListener<R>?): String {
        val taskId = UUID.randomUUID().toString()
        val scope = WorkGraphCoroutineScope<V, R>(taskId, listener)

        this.taskExecutors[taskId] = scope

        val entry = this.getEntry()
        scope.launch(Dispatchers.IO + CoroutineName("Node#${entry.nodeName}")) {
            this@AbstractWorkFlowGraph.executeNode(scope, inputData, entry)
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

    fun getTaskExecutor(taskId: String): WorkGraphCoroutineScope<V, R> {
        return this.taskExecutors[taskId] ?: throw IllegalArgumentException("Task $taskId not found")
    }

    fun clear() {
        this.graphNodeMap.clear()
        this.graphNodeTriggerEdges.clear()
        this.graphNodeParameterTransmissionEdges.clear()
        this.graphNodeExecutionResult.clear()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun getNodeById(nodeId: String): V {
        val node = this.graphNodeMap[nodeId]
        return node ?: throw IllegalArgumentException("Node $nodeId not found in graph")
    }
}