package com.lovelycatv.vertex.ai.workflow.graph

import com.lovelycatv.vertex.ai.workflow.WorkFlowGraphConstants.DEFAULT_EDGE_GROUP
import com.lovelycatv.vertex.ai.workflow.graph.edge.GraphNodeParameterTransmissionEdge
import com.lovelycatv.vertex.ai.workflow.graph.edge.GraphTriggerEdge
import com.lovelycatv.vertex.ai.workflow.graph.node.AbstractGraphNode
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeEntry
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeExit
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AbstractWorkFlowGraphTest {
    private class ExposedGraph<R : Any>(graphName: String) : AbstractWorkFlowGraph<AbstractGraphNode, R>(graphName) {
        fun exposedGraphNodeMap(): MutableMap<String, AbstractGraphNode> = graphNodeMap
        fun exposedTriggerEdges(): MutableList<GraphTriggerEdge> = graphNodeTriggerEdges
        fun exposedParameterTransmissionEdges(): MutableList<GraphNodeParameterTransmissionEdge> = graphNodeParameterTransmissionEdges
        fun exposedExecutionResult(): MutableMap<String, Map<GraphNodeParameter, Any?>> = graphNodeExecutionResult
        fun exposedTaskExecutors(): MutableMap<String, WorkGraphCoroutineScope<AbstractGraphNode, R>> = taskExecutors

        fun exposedGetNodeById(nodeId: String): AbstractGraphNode = getNodeById(nodeId)

        suspend fun exposedExecuteNode(
            scope: WorkGraphCoroutineScope<AbstractGraphNode, R>,
            inputData: Map<GraphNodeParameter, Any?>,
            node: AbstractGraphNode
        ) {
            executeNode(scope, inputData, node)
        }
    }

    private class TestNode(
        nodeId: String,
        nodeName: String,
        inputs: List<GraphNodeParameter>,
        outputs: List<GraphNodeParameter>,
        private val triggerGroups: List<String> = listOf(DEFAULT_EDGE_GROUP),
        private val executor: suspend (Map<GraphNodeParameter, Any?>) -> Map<GraphNodeParameter, Any?>
    ) : AbstractGraphNode(
        nodeType = GraphNodeType.ADD,
        nodeId = nodeId,
        nodeName = nodeName,
        inputs = inputs,
        outputs = outputs
    ) {
        override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
            return executor(inputData)
        }

        override fun determineTriggerGroups(inputData: Map<GraphNodeParameter, Any?>): List<String> {
            return triggerGroups
        }
    }

    private class RecordingListener<R : Any> : WorkFlowGraphListener<R> {
        val startedTaskIds = mutableListOf<String>()
        val finished = CompletableDeferred<Pair<String, R?>>()
        val cancelledTaskIds = mutableListOf<String>()

        override fun onTaskStarted(taskId: String) {
            startedTaskIds.add(taskId)
        }

        override fun onTaskFinished(taskId: String, outputData: R?) {
            if (!finished.isCompleted) {
                finished.complete(taskId to outputData)
            }
        }

        override fun onTaskCancelled(taskId: String) {
            cancelledTaskIds.add(taskId)
        }
    }

    private fun p(type: KClass<*>, name: String): GraphNodeParameter = GraphNodeParameter(type, name)

    @Test
    fun getGraphNodeMap() {
        val graph = ExposedGraph<Int>("G")
        val node = GraphNodeEntry(nodeId = "entry", nodeName = "entry", inputs = listOf(p(Int::class, "x")))

        graph.addNode(node)

        assertSame(node, graph.exposedGraphNodeMap()["entry"])
    }

    @Test
    fun getGraphNodeTriggerEdges() {
        val graph = ExposedGraph<Int>("G")
        graph.addNode(GraphNodeEntry(nodeId = "a", nodeName = "a", inputs = emptyList()))
        graph.addNode(GraphNodeExit(nodeId = "b", nodeName = "b", outputValueType = Int::class, strict = true))

        graph.addTriggerEdge("a", "b")
        graph.addTriggerEdge("a", "b", groupId = "G1")

        val edges = graph.exposedTriggerEdges()
        assertEquals(2, edges.size)
        assertTrue(edges.any { it.from == "a" && it.to == "b" && it.groupId == DEFAULT_EDGE_GROUP })
        assertTrue(edges.any { it.from == "a" && it.to == "b" && it.groupId == "G1" })
    }

    @Test
    fun getGraphNodeParameterTransmissionEdges() {
        val graph = ExposedGraph<Int>("G")
        val fromParam = p(Int::class, "out")
        val toParam = p(Number::class, "in")

        val fromNode = TestNode(
            nodeId = "from",
            nodeName = "from",
            inputs = listOf(fromParam),
            outputs = listOf(fromParam)
        ) { mapOf(fromParam to 1) }

        val toNode = TestNode(
            nodeId = "to",
            nodeName = "to",
            inputs = listOf(toParam),
            outputs = listOf(p(Int::class, "unused"))
        ) { emptyMap() }

        graph.addNode(fromNode)
        graph.addNode(toNode)

        graph.addParameterTransmissionEdge("from", "to", fromParam, toParam)

        val edges = graph.exposedParameterTransmissionEdges()
        assertEquals(1, edges.size)
        assertEquals("from", edges.first().from)
        assertEquals("to", edges.first().to)
    }

    @Test
    fun getGraphNodeExecutionResult() = runBlocking {
        val graph = ExposedGraph<Int>("G")
        val outputParam = p(Int::class, GraphNodeExit.OUTPUT)
        val exit = GraphNodeExit(nodeId = "exit", nodeName = "exit", outputValueType = Int::class, strict = true)
        graph.addNode(exit)

        val listener = RecordingListener<Int>()
        val scope = WorkGraphCoroutineScope<AbstractGraphNode, Int>("task", listener)

        graph.exposedExecuteNode(scope, mapOf(outputParam to 123), exit)

        assertEquals(1, graph.exposedExecutionResult().size)
        assertNotNull(graph.exposedExecutionResult()["exit"])
        withTimeout(1_000) { listener.finished.await() }

        scope.cancel()
    }

    @Test
    fun getTaskExecutors() {
        val graph = ExposedGraph<Int>("G")
        val entryParam = p(Int::class, "x")
        val entry = GraphNodeEntry(nodeId = "entry", nodeName = "entry", inputs = listOf(entryParam))
        val exit = GraphNodeExit(nodeId = "exit", nodeName = "exit", outputValueType = Int::class, strict = true)
        graph.addNode(entry)
        graph.addNode(exit)
        graph.addTriggerEdge("entry", "exit")
        graph.addParameterTransmissionEdge("entry", "exit", "x", GraphNodeExit.OUTPUT)

        val taskId = graph.start(mapOf(entryParam to 1), listener = null)

        assertTrue(graph.exposedTaskExecutors().containsKey(taskId))

        graph.getTaskExecutor(taskId).cancel()
    }

    @Test
    fun addNode() {
        val graph = ExposedGraph<Int>("G")
        val original = GraphNodeEntry(nodeId = "n", nodeName = "n", inputs = emptyList())
        val replacement = GraphNodeEntry(nodeId = "n", nodeName = "n2", inputs = emptyList())

        graph.addNode(original)
        graph.addNode(replacement)

        assertSame(replacement, graph.exposedGraphNodeMap()["n"])
    }

    @Test
    fun removeNode() {
        val graph = ExposedGraph<Int>("G")
        val n1 = GraphNodeEntry(nodeId = "n1", nodeName = "n1", inputs = emptyList())
        val n2 = GraphNodeEntry(nodeId = "n2", nodeName = "n2", inputs = emptyList())

        val edgeParam = p(Int::class, "p")
        val n3 = TestNode("n3", "n3", inputs = listOf(edgeParam), outputs = listOf(edgeParam)) { mapOf(edgeParam to 1) }
        val n4 = TestNode("n4", "n4", inputs = listOf(edgeParam), outputs = listOf(edgeParam)) { emptyMap() }

        graph.addNode(n1)
        graph.addNode(n2)
        graph.addNode(n3)
        graph.addNode(n4)

        graph.addTriggerEdge("n1", "n2")
        graph.addTriggerEdge("n2", "n1", groupId = "g")
        graph.addParameterTransmissionEdge("n3", "n4", "p", "p")

        graph.removeNode(n1)

        assertNull(graph.exposedGraphNodeMap()["n1"])
        assertTrue(graph.exposedTriggerEdges().none { it.from == "n1" || it.to == "n1" })

        graph.removeNode(n4)
        assertTrue(graph.exposedParameterTransmissionEdges().none { it.from == "n4" || it.to == "n4" })
    }

    @Test
    fun addTriggerEdge() {
        val graph = ExposedGraph<Int>("G")
        graph.addNode(GraphNodeEntry(nodeId = "a", nodeName = "a", inputs = emptyList()))
        graph.addNode(GraphNodeEntry(nodeId = "b", nodeName = "b", inputs = emptyList()))

        graph.addTriggerEdge(GraphTriggerEdge("a", "b", DEFAULT_EDGE_GROUP))

        assertEquals(1, graph.exposedTriggerEdges().size)
    }

    @Test
    fun testAddTriggerEdge() {
        val graph = ExposedGraph<Int>("G")
        graph.addNode(GraphNodeEntry(nodeId = "a", nodeName = "a", inputs = emptyList()))

        assertFailsWith<IllegalArgumentException> {
            graph.addTriggerEdge("a", "missing")
        }
    }

    @Test
    fun removeTriggerEdge() {
        val graph = ExposedGraph<Int>("G")
        graph.addNode(GraphNodeEntry(nodeId = "a", nodeName = "a", inputs = emptyList()))
        graph.addNode(GraphNodeEntry(nodeId = "b", nodeName = "b", inputs = emptyList()))

        graph.addTriggerEdge("a", "b")
        graph.addTriggerEdge("a", "b", groupId = "g")

        graph.removeTriggerEdge("a", "b")
        assertTrue(graph.exposedTriggerEdges().all { it.groupId != DEFAULT_EDGE_GROUP })

        graph.removeTriggerEdge("a", "b", groupId = "g")
        assertTrue(graph.exposedTriggerEdges().isEmpty())
    }

    @Test
    fun addParameterTransmissionEdge() {
        val graph = ExposedGraph<Int>("G")
        val fromParam = p(Int::class, "out")
        val toParam = p(Number::class, "in")

        val fromNode = TestNode(
            nodeId = "from",
            nodeName = "from",
            inputs = listOf(fromParam),
            outputs = listOf(fromParam)
        ) { mapOf(fromParam to 1) }

        val toNode = TestNode(
            nodeId = "to",
            nodeName = "to",
            inputs = listOf(toParam),
            outputs = listOf(p(Int::class, "unused"))
        ) { emptyMap() }

        graph.addNode(fromNode)
        graph.addNode(toNode)

        graph.addParameterTransmissionEdge(GraphNodeParameterTransmissionEdge("from", "to", "out", "in"))

        assertEquals(1, graph.exposedParameterTransmissionEdges().size)
    }

    @Test
    fun testAddParameterTransmissionEdge() {
        val graph = ExposedGraph<Int>("G")
        val fromParam = p(String::class, "out")
        val toParam = p(Int::class, "in")

        val fromNode = TestNode(
            nodeId = "from",
            nodeName = "from",
            inputs = listOf(fromParam),
            outputs = listOf(fromParam)
        ) { emptyMap() }

        val toNode = TestNode(
            nodeId = "to",
            nodeName = "to",
            inputs = listOf(toParam),
            outputs = listOf(p(Int::class, "unused"))
        ) { emptyMap() }

        graph.addNode(fromNode)
        graph.addNode(toNode)

        assertFailsWith<IllegalArgumentException> {
            graph.addParameterTransmissionEdge("from", "to", "missingOut", "in")
        }

        assertFailsWith<IllegalArgumentException> {
            graph.addParameterTransmissionEdge("from", "to", "out", "missingIn")
        }

        assertFailsWith<IllegalArgumentException> {
            graph.addParameterTransmissionEdge("from", "to", "out", "in")
        }
    }

    @Test
    fun testAddParameterTransmissionEdge1() {
        val graph = ExposedGraph<Int>("G")
        val fromParam1 = p(Int::class, "out1")
        val fromParam2 = p(Int::class, "out2")
        val toParam = p(Int::class, "in")

        val fromNode1 = TestNode("from1", "from1", inputs = listOf(fromParam1), outputs = listOf(fromParam1)) { emptyMap() }
        val fromNode2 = TestNode("from2", "from2", inputs = listOf(fromParam2), outputs = listOf(fromParam2)) { emptyMap() }
        val toNode = TestNode("to", "to", inputs = listOf(toParam), outputs = listOf(p(Int::class, "unused"))) { emptyMap() }

        graph.addNode(fromNode1)
        graph.addNode(fromNode2)
        graph.addNode(toNode)

        graph.addParameterTransmissionEdge("from1", "to", "out1", "in")

        assertFailsWith<IllegalStateException> {
            graph.addParameterTransmissionEdge("from2", "to", "out2", "in")
        }
    }

    @Test
    fun removeParameterTransmissionEdge() {
        val graph = ExposedGraph<Int>("G")
        val fromParam = p(Int::class, "out")
        val toParam = p(Int::class, "in")

        val fromNode = TestNode("from", "from", inputs = listOf(fromParam), outputs = listOf(fromParam)) { emptyMap() }
        val toNode = TestNode("to", "to", inputs = listOf(toParam), outputs = listOf(p(Int::class, "unused"))) { emptyMap() }

        graph.addNode(fromNode)
        graph.addNode(toNode)

        graph.addParameterTransmissionEdge("from", "to", fromParam, toParam)
        graph.removeParameterTransmissionEdge("from", "to", fromParam, toParam)

        assertTrue(graph.exposedParameterTransmissionEdges().isEmpty())
    }

    @Test
    fun testRemoveParameterTransmissionEdge() {
        val graph = ExposedGraph<Int>("G")
        val fromParam = p(Int::class, "out")
        val toParam = p(Int::class, "in")

        val fromNode = TestNode("from", "from", inputs = listOf(fromParam), outputs = listOf(fromParam)) { emptyMap() }
        val toNode = TestNode("to", "to", inputs = listOf(toParam), outputs = listOf(p(Int::class, "unused"))) { emptyMap() }

        graph.addNode(fromNode)
        graph.addNode(toNode)

        graph.addParameterTransmissionEdge("from", "to", "out", "in")
        graph.removeParameterTransmissionEdge("from", "to", "out", "in")
        graph.removeParameterTransmissionEdge("from", "to", "out", "in")

        assertTrue(graph.exposedParameterTransmissionEdges().isEmpty())
    }

    @Test
    fun queryEdges() {
        val graph = ExposedGraph<Int>("G")
        val shared = p(Int::class, "shared")
        val input = p(Int::class, "in")
        val targetInput = p(Int::class, "target")

        val n1 = TestNode("n1", "n1", inputs = listOf(shared), outputs = listOf(shared)) { mapOf(shared to 1) }
        val n2 = TestNode("n2", "n2", inputs = listOf(input), outputs = listOf(input)) { emptyMap() }
        val n3 = TestNode("n3", "n3", inputs = listOf(targetInput), outputs = emptyList()) { emptyMap() }

        graph.addNode(n1)
        graph.addNode(n2)
        graph.addNode(n3)

        graph.addTriggerEdge("n1", "n2", groupId = "g1")
        graph.addTriggerEdge("n1", "n3", groupId = "g2")
        graph.addParameterTransmissionEdge("n1", "n2", "shared", "in")
        graph.addParameterTransmissionEdge("n2", "n3", "in", "target")

        val result = graph.queryEdges(n2)
        assertEquals(listOf("n1"), result.triggerOrigins)
        assertTrue(result.triggerTargets.isEmpty())
        assertEquals(1, result.parameterOrigins.size)
        assertNotNull(result.parameterOrigins["in"])
        assertEquals(1, result.parameterOutputs["in"]!!.size)

        val n1Query = graph.queryEdges(n1)
        assertEquals(mapOf("g1" to listOf("n2"), "g2" to listOf("n3")), n1Query.triggerTargets)
    }

    @Test
    fun testQueryEdges() {
        val graph = ExposedGraph<Int>("G")
        val node = GraphNodeEntry(nodeId = "entry", nodeName = "entry", inputs = emptyList())
        graph.addNode(node)

        assertSame(node, graph.queryEdges("entry").node)

        assertFailsWith<IllegalArgumentException> {
            graph.queryEdges("missing")
        }
    }

    @Test
    fun getEntry() {
        val graph = ExposedGraph<Int>("G")

        assertFailsWith<IllegalStateException> {
            graph.getEntry()
        }

        graph.addNode(GraphNodeEntry(nodeId = "e1", nodeName = "e1", inputs = emptyList()))
        graph.addNode(GraphNodeEntry(nodeId = "e2", nodeName = "e2", inputs = emptyList()))

        assertFailsWith<IllegalStateException> {
            graph.getEntry()
        }

        graph.clear()
        val entry = GraphNodeEntry(nodeId = "e", nodeName = "e", inputs = emptyList())
        graph.addNode(entry)
        assertSame(entry, graph.getEntry())
    }

    @Test
    fun getOutputType() {
        val graph = ExposedGraph<Number>("G")

        assertFailsWith<IllegalStateException> {
            graph.getOutputType()
        }

        graph.addNode(GraphNodeExit(nodeId = "x1", nodeName = "x1", outputValueType = Int::class))
        graph.addNode(GraphNodeExit(nodeId = "x2", nodeName = "x2", outputValueType = Double::class))

        assertFailsWith<IllegalStateException> {
            graph.getOutputType()
        }

        graph.clear()
        graph.addNode(GraphNodeExit(nodeId = "x", nodeName = "x", outputValueType = Number::class))
        assertEquals(Number::class, graph.getOutputType())
    }

    @Test
    fun executeNode() = runBlocking {
        val graph = ExposedGraph<Int>("G")
        val listener = RecordingListener<Int>()
        val scope = WorkGraphCoroutineScope<AbstractGraphNode, Int>("task", listener)

        val exit = GraphNodeExit(nodeId = "exit", nodeName = "exit", outputValueType = Int::class, strict = true)
        graph.addNode(exit)

        graph.exposedExecuteNode(scope, mapOf(p(Int::class, GraphNodeExit.OUTPUT) to 42), exit)
        withTimeout(1_000) {
            val (_, output) = listener.finished.await()
            assertEquals(42, output)
        }

        val node = TestNode(
            nodeId = "n",
            nodeName = "n",
            inputs = emptyList(),
            outputs = emptyList()
        ) { emptyMap() }
        graph.addNode(node)

        assertFailsWith<IllegalStateException> {
            graph.exposedExecuteNode(scope, emptyMap(), node)
        }

        val entryParam = p(Int::class, "x")
        val entry = GraphNodeEntry(nodeId = "entry", nodeName = "entry", inputs = listOf(entryParam))
        val nodeBInputA = p(Int::class, "a")
        val nodeBInputB = p(Int::class, "b")
        val nodeBInputC = p(Int::class, "c")
        val nodeBOutput = p(Int::class, "out")

        val nodeXOut = p(Int::class, "out")
        val nodeX = TestNode("x", "x", inputs = listOf(nodeXOut), outputs = listOf(nodeXOut)) { emptyMap() }
        val nodeYOut = p(Int::class, "out")
        val nodeY = TestNode("y", "y", inputs = listOf(nodeYOut), outputs = listOf(nodeYOut)) { emptyMap() }

        val nodeB = TestNode(
            nodeId = "b",
            nodeName = "b",
            inputs = listOf(nodeBInputA, nodeBInputB, nodeBInputC),
            outputs = listOf(nodeBOutput)
        ) { inputData ->
            assertTrue(inputData.keys.any { it.name == "entry.x" })
            assertEquals(7, inputData[nodeBInputA])
            assertNull(inputData[nodeBInputB])
            assertNull(inputData[nodeBInputC])
            mapOf(nodeBOutput to 99)
        }

        val exit2 = GraphNodeExit(nodeId = "exit2", nodeName = "exit2", outputValueType = Int::class, strict = true)

        graph.addNode(entry)
        graph.addNode(nodeX)
        graph.addNode(nodeY)
        graph.addNode(nodeB)
        graph.addNode(exit2)

        graph.addTriggerEdge("entry", "b", groupId = DEFAULT_EDGE_GROUP)
        graph.addTriggerEdge("entry", "x", groupId = "NOT_TRIGGERED")
        graph.addTriggerEdge("b", "exit2", groupId = DEFAULT_EDGE_GROUP)

        graph.addParameterTransmissionEdge("entry", "b", "x", "a")
        graph.addParameterTransmissionEdge("x", "b", "out", "b")
        graph.addParameterTransmissionEdge("y", "b", "out", "c")
        graph.addParameterTransmissionEdge("b", "exit2", "out", GraphNodeExit.OUTPUT)

        graph.exposedExecutionResult()["x"] = mapOf(p(Int::class, "wrong") to 123)

        val finished2 = CompletableDeferred<Int?>()
        val scope2 = WorkGraphCoroutineScope<AbstractGraphNode, Int>(
            "task2",
            object : WorkFlowGraphListener<Int> {
                override fun onTaskStarted(taskId: String) {}
                override fun onTaskFinished(taskId: String, outputData: Int?) {
                    if (!finished2.isCompleted) finished2.complete(outputData)
                }

                override fun onTaskCancelled(taskId: String) {}
            }
        )

        graph.exposedExecuteNode(scope2, mapOf(entryParam to 7), entry)

        withTimeout(2_000) {
            assertEquals(99, finished2.await())
        }
        scope2.cancel()

        scope.cancel()
    }

    @Test
    fun start() = runBlocking {
        val graph = ExposedGraph<Int>("G")
        val entryParam = p(Int::class, "x")
        val entry = GraphNodeEntry(nodeId = "entry", nodeName = "entry", inputs = listOf(entryParam))
        val exit = GraphNodeExit(nodeId = "exit", nodeName = "exit", outputValueType = Int::class, strict = true)

        graph.addNode(entry)
        graph.addNode(exit)
        graph.addTriggerEdge("entry", "exit")
        graph.addParameterTransmissionEdge("entry", "exit", "x", GraphNodeExit.OUTPUT)

        val listener = RecordingListener<Int>()
        val taskId = graph.start(mapOf(entryParam to 7), listener)

        assertEquals(listOf(taskId), listener.startedTaskIds)
        assertTrue(graph.exposedTaskExecutors().containsKey(taskId))

        graph.getTaskExecutor(taskId).cancel()
    }

    @Test
    fun stop() {
        val graph = ExposedGraph<Int>("G")
        val entryParam = p(Int::class, "x")
        val entry = GraphNodeEntry(nodeId = "entry", nodeName = "entry", inputs = listOf(entryParam))
        val exit = GraphNodeExit(nodeId = "exit", nodeName = "exit", outputValueType = Int::class, strict = true)

        graph.addNode(entry)
        graph.addNode(exit)
        graph.addTriggerEdge("entry", "exit")
        graph.addParameterTransmissionEdge("entry", "exit", "x", GraphNodeExit.OUTPUT)

        val listener = RecordingListener<Int>()
        val taskId = graph.start(mapOf(entryParam to 7), listener)

        graph.stop(taskId)
        assertTrue(listener.cancelledTaskIds.contains(taskId))

        graph.stop("missing")
    }

    @Test
    fun awaitTask() = runBlocking {
        val graph = ExposedGraph<Int>("G")

        graph.awaitTask("missing")

        val entryParam = p(Int::class, "x")
        val entry = GraphNodeEntry(nodeId = "entry", nodeName = "entry", inputs = listOf(entryParam))
        val exit = GraphNodeExit(nodeId = "exit", nodeName = "exit", outputValueType = Int::class, strict = true)

        graph.addNode(entry)
        graph.addNode(exit)
        graph.addTriggerEdge("entry", "exit")
        graph.addParameterTransmissionEdge("entry", "exit", "x", GraphNodeExit.OUTPUT)

        val taskId = graph.start(mapOf(entryParam to 1), listener = null)
        val awaiting = async {
            graph.awaitTask(taskId)
        }

        delay(150)
        graph.stop(taskId)

        withTimeout(2_000) { awaiting.await() }
    }

    @Test
    fun getTaskExecutor() {
        val graph = ExposedGraph<Int>("G")

        assertFailsWith<IllegalArgumentException> {
            graph.getTaskExecutor("missing")
        }

        val entryParam = p(Int::class, "x")
        val entry = GraphNodeEntry(nodeId = "entry", nodeName = "entry", inputs = listOf(entryParam))
        val exit = GraphNodeExit(nodeId = "exit", nodeName = "exit", outputValueType = Int::class, strict = true)

        graph.addNode(entry)
        graph.addNode(exit)
        graph.addTriggerEdge("entry", "exit")
        graph.addParameterTransmissionEdge("entry", "exit", "x", GraphNodeExit.OUTPUT)

        val taskId = graph.start(mapOf(entryParam to 1), listener = null)
        assertNotNull(graph.getTaskExecutor(taskId))

        graph.getTaskExecutor(taskId).cancel()
    }

    @Test
    fun clear() {
        val graph = ExposedGraph<Int>("G")
        val entry = GraphNodeEntry(nodeId = "entry", nodeName = "entry", inputs = emptyList())
        graph.addNode(entry)

        graph.addNode(GraphNodeExit(nodeId = "exit", nodeName = "exit", outputValueType = Int::class, strict = true))
        graph.addTriggerEdge("entry", "exit")

        graph.clear()

        assertTrue(graph.exposedGraphNodeMap().isEmpty())
        assertTrue(graph.exposedTriggerEdges().isEmpty())
        assertTrue(graph.exposedParameterTransmissionEdges().isEmpty())
        assertTrue(graph.exposedExecutionResult().isEmpty())
    }

    @Test
    fun getNodeById() {
        val graph = ExposedGraph<Int>("G")
        val node = GraphNodeEntry(nodeId = "entry", nodeName = "entry", inputs = emptyList())
        graph.addNode(node)

        assertSame(node, graph.exposedGetNodeById("entry"))

        assertFailsWith<IllegalArgumentException> {
            graph.exposedGetNodeById("missing")
        }
    }

    @Test
    fun getGraphName() {
        val graph = ExposedGraph<Int>("MyGraph")
        assertEquals("MyGraph", graph.graphName)
    }
}
