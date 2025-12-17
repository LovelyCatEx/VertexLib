package com.lovelycatv.vertex.ai.workflow.graph

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.workflow.WorkFlowGraphConstants.DEFAULT_EDGE_GROUP
import com.lovelycatv.vertex.ai.workflow.graph.edge.GraphNodeParameterTransmissionEdge
import com.lovelycatv.vertex.ai.workflow.graph.edge.GraphTriggerEdge
import com.lovelycatv.vertex.ai.workflow.graph.node.*
import com.lovelycatv.vertex.ai.workflow.graph.serializer.GraphNodeDeserializer
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class AbstractWorkFlowGraphTest {
    @Test
    fun addNode() {
        val graph = WorkFlowGraph("TestGraph")

        val input = GraphNodeParameter(String::class, "value")
        graph.addNode(GraphNodeEntry("entry", "Entry1", listOf(input)))
        assertEquals("Entry1", graph.getEntry().nodeName)

        graph.addNode(GraphNodeEntry("entry", "Entry2", listOf(input)))
        assertEquals("Entry2", graph.getEntry().nodeName)
    }

    @Test
    fun removeNode() {
        val graph = WorkFlowGraph("TestGraph")

        val payload = GraphNodeParameter(String::class, "payload")

        val entry = GraphNodeEntry("entry", "Entry", listOf(payload))
        val exit = GraphNodeExit("exit", "Exit", listOf(payload), strict = true)
        val other = GraphNodeExit("other", "Other", listOf(payload), strict = true)

        graph.addNode(entry)
        graph.addNode(exit)
        graph.addNode(other)

        graph.addTriggerEdge(entry.nodeId, exit.nodeId)
        graph.addParameterTransmissionEdge(entry.nodeId, exit.nodeId, "payload", "payload")

        graph.addTriggerEdge(other.nodeId, entry.nodeId, "back")
        graph.addParameterTransmissionEdge(other.nodeId, entry.nodeId, "payload", "payload")

        graph.removeNode(entry)

        assertThrows(IllegalArgumentException::class.java) { graph.queryEdges("entry") }

        val exitEdges = graph.queryEdges(exit)
        assertTrue(exitEdges.triggerOrigins.isEmpty())
        assertNull(exitEdges.parameterOrigins["payload"])

        val otherEdges = graph.queryEdges(other)
        assertTrue(otherEdges.triggerTargets.isEmpty())
        assertEquals(emptyList<GraphNodeParameterTransmissionEdge>(), otherEdges.parameterOutputs["payload"])
    }

    @Test
    fun addTriggerEdge() {
        val graph = WorkFlowGraph("TestGraph")

        val p = GraphNodeParameter(String::class, "p")
        val entry = GraphNodeEntry("entry", "Entry", listOf(p))
        val exit = GraphNodeExit("exit", "Exit", listOf(p))

        graph.addNode(entry)
        graph.addNode(exit)

        graph.addTriggerEdge(GraphTriggerEdge("entry", "exit", "custom"))

        val entryEdges = graph.queryEdges(entry)
        assertEquals(listOf("exit"), entryEdges.triggerTargets["custom"])

        val exitEdges = graph.queryEdges(exit)
        assertEquals(listOf("entry"), exitEdges.triggerOrigins)
    }

    @Test
    fun testAddTriggerEdge() {
        val graph = WorkFlowGraph("TestGraph")

        val p = GraphNodeParameter(String::class, "p")
        val entry = GraphNodeEntry("entry", "Entry", listOf(p))
        val exit = GraphNodeExit("exit", "Exit", listOf(p))

        graph.addNode(entry)
        graph.addNode(exit)

        graph.addTriggerEdge("entry", "exit")

        val entryEdges = graph.queryEdges("entry")
        assertEquals(listOf("exit"), entryEdges.triggerTargets[DEFAULT_EDGE_GROUP])

        assertThrows(IllegalArgumentException::class.java) { graph.addTriggerEdge("missing", "exit") }
        assertThrows(IllegalArgumentException::class.java) { graph.addTriggerEdge("entry", "missing") }
    }

    @Test
    fun removeTriggerEdge() {
        val graph = WorkFlowGraph("TestGraph")

        val p = GraphNodeParameter(String::class, "p")
        val entry = GraphNodeEntry("entry", "Entry", listOf(p))
        val exit = GraphNodeExit("exit", "Exit", listOf(p))

        graph.addNode(entry)
        graph.addNode(exit)

        graph.addTriggerEdge("entry", "exit")
        graph.addTriggerEdge("entry", "exit", "g1")

        graph.removeTriggerEdge("entry", "exit")
        assertFalse(graph.queryEdges(entry).triggerTargets.containsKey(DEFAULT_EDGE_GROUP))
        assertEquals(listOf("exit"), graph.queryEdges(entry).triggerTargets["g1"])

        graph.removeTriggerEdge("entry", "exit", "g1")
        assertTrue(graph.queryEdges(entry).triggerTargets.isEmpty())

        assertDoesNotThrow { graph.removeTriggerEdge("entry", "exit", "not-exist") }
    }

    @Test
    fun addParameterTransmissionEdge() {
        val graph = WorkFlowGraph("TestGraph")

        val out = GraphNodeParameter(String::class, "out")
        val input = GraphNodeParameter(String::class, "in")

        val entry = GraphNodeEntry("entry", "Entry", listOf(out))
        val exit = GraphNodeExit("exit", "Exit", listOf(input))

        graph.addNode(entry)
        graph.addNode(exit)

        graph.addParameterTransmissionEdge(GraphNodeParameterTransmissionEdge("entry", "exit", "out", "in"))

        val edge = graph.queryEdges(exit).parameterOrigins["in"]
        assertNotNull(edge)
        assertEquals("entry", edge!!.from)
        assertEquals("exit", edge.to)
        assertEquals("out", edge.fromParameterName)
        assertEquals("in", edge.toParameterName)
    }

    @Test
    fun testAddParameterTransmissionEdge() {
        run {
            val graph = WorkFlowGraph("TestGraph")

            val a = GraphNodeParameter(String::class, "a")
            val b = GraphNodeParameter(String::class, "b")

            graph.addNode(GraphNodeEntry("entry", "Entry", listOf(a)))
            graph.addNode(GraphNodeExit("exit", "Exit", listOf(b)))

            graph.addParameterTransmissionEdge("entry", "exit", "a", "b")
            assertNotNull(graph.queryEdges("exit").parameterOrigins["b"])
        }

        run {
            val graph = WorkFlowGraph("TestGraph")

            val a = GraphNodeParameter(String::class, "a")
            val b = GraphNodeParameter(String::class, "b")

            graph.addNode(GraphNodeEntry("entry", "Entry", listOf(a)))
            graph.addNode(GraphNodeExit("exit", "Exit", listOf(b)))

            assertThrows(IllegalArgumentException::class.java) {
                graph.addParameterTransmissionEdge("entry", "exit", "missing", "b")
            }
        }

        run {
            val graph = WorkFlowGraph("TestGraph")

            val a = GraphNodeParameter(String::class, "a")
            val b = GraphNodeParameter(String::class, "b")

            graph.addNode(GraphNodeEntry("entry", "Entry", listOf(a)))
            graph.addNode(GraphNodeExit("exit", "Exit", listOf(b)))

            assertThrows(IllegalArgumentException::class.java) {
                graph.addParameterTransmissionEdge("entry", "exit", "a", "missing")
            }
        }

        run {
            val graph = WorkFlowGraph("TestGraph")

            val a = GraphNodeParameter(String::class, "a")
            val b = GraphNodeParameter(Int::class, "b")

            graph.addNode(GraphNodeEntry("entry", "Entry", listOf(a)))
            graph.addNode(GraphNodeExit("exit", "Exit", listOf(b)))

            assertThrows(IllegalArgumentException::class.java) {
                graph.addParameterTransmissionEdge("entry", "exit", "a", "b")
            }
        }
    }

    @Test
    fun removeParameterTransmissionEdge() {
        val graph = WorkFlowGraph("TestGraph")

        val a = GraphNodeParameter(String::class, "a")
        val b = GraphNodeParameter(String::class, "b")
        val c = GraphNodeParameter(String::class, "c")
        val d = GraphNodeParameter(String::class, "d")

        val entry = GraphNodeEntry("entry", "Entry", listOf(a, c))
        val exit = GraphNodeExit("exit", "Exit", listOf(b, d))

        graph.addNode(entry)
        graph.addNode(exit)

        graph.addParameterTransmissionEdge("entry", "exit", "a", "b")
        graph.addParameterTransmissionEdge("entry", "exit", "c", "d")

        graph.removeParameterTransmissionEdge("entry", "exit", "a", "b")
        val edges = graph.queryEdges(exit).parameterOrigins
        assertNull(edges["b"])
        assertNotNull(edges["d"])

        assertDoesNotThrow { graph.removeParameterTransmissionEdge("entry", "exit", "not-exist", "b") }
    }

    @Test
    fun queryEdges() {
        val graph = WorkFlowGraph("TestGraph")

        val condition = GraphNodeParameter(Boolean::class, GraphNodeIf.INPUT_CONDITION)
        val entry = GraphNodeEntry("entry", "Entry", listOf(condition))
        val ifNode = GraphNodeIf("if", "If")

        val passExit = GraphNodeExit("pass", "Pass", listOf(GraphNodeParameter(String::class, "out")), strict = true)
        val failExit = GraphNodeExit("fail", "Fail", listOf(GraphNodeParameter(String::class, "out")), strict = true)

        graph.addNode(entry)
        graph.addNode(ifNode)
        graph.addNode(passExit)
        graph.addNode(failExit)

        graph.addTriggerEdge("entry", "if")
        graph.addTriggerEdge("if", "pass", GraphNodeIf.GROUP_PASSED)
        graph.addTriggerEdge("if", "fail", GraphNodeIf.GROUP_FAILED)
        graph.addParameterTransmissionEdge("entry", "if", GraphNodeIf.INPUT_CONDITION, GraphNodeIf.INPUT_CONDITION)

        val result = graph.queryEdges("if")

        assertEquals("if", result.node.nodeId)
        assertEquals(listOf("entry"), result.triggerOrigins)
        assertEquals(listOf("pass"), result.triggerTargets[GraphNodeIf.GROUP_PASSED])
        assertEquals(listOf("fail"), result.triggerTargets[GraphNodeIf.GROUP_FAILED])

        assertNotNull(result.parameterOrigins[GraphNodeIf.INPUT_CONDITION])
        assertEquals(1, result.parameterOutputs[GraphNodeIf.INPUT_CONDITION]!!.size)
    }

    @Test
    fun testQueryEdges() {
        val graph = WorkFlowGraph("TestGraph")

        val p = GraphNodeParameter(String::class, "p")
        val entry = GraphNodeEntry("entry", "Entry", listOf(p))
        val exit = GraphNodeExit("exit", "Exit", listOf(p))

        graph.addNode(entry)
        graph.addNode(exit)
        graph.addTriggerEdge("entry", "exit")

        val ghost = GraphNodeExit("ghost", "Ghost", listOf(GraphNodeParameter(String::class, "x")))
        val result = graph.queryEdges(ghost)

        assertSame(ghost, result.node)
        assertTrue(result.triggerOrigins.isEmpty())
        assertTrue(result.triggerTargets.isEmpty())
        assertNull(result.parameterOrigins["x"])
        assertEquals(emptyList<GraphNodeParameterTransmissionEdge>(), result.parameterOutputs["x"])
    }

    @Test
    fun getEntry() {
        run {
            val graph = WorkFlowGraph("TestGraph")
            val p = GraphNodeParameter(String::class, "p")
            graph.addNode(GraphNodeExit("exit", "Exit", listOf(p)))
            assertThrows(IllegalStateException::class.java) { graph.getEntry() }
        }

        run {
            val graph = WorkFlowGraph("TestGraph")
            val p = GraphNodeParameter(String::class, "p")
            graph.addNode(GraphNodeEntry("entry", "Entry", listOf(p)))
            graph.addNode(GraphNodeExit("exit", "Exit", listOf(p)))
            assertEquals("entry", graph.getEntry().nodeId)
        }

        run {
            val graph = WorkFlowGraph("TestGraph")
            val p = GraphNodeParameter(String::class, "p")
            graph.addNode(GraphNodeEntry("entry1", "Entry1", listOf(p)))
            graph.addNode(GraphNodeEntry("entry2", "Entry2", listOf(p)))
            assertThrows(IllegalStateException::class.java) { graph.getEntry() }
        }
    }

    @Test
    fun executeNode() {
        runBlocking {
            val graph = TestGraph("TestGraph")

            val out = GraphNodeParameter(String::class, "out")
            val exit = GraphNodeExit("exit", "Exit", listOf(out), strict = true)
            graph.addNode(exit)

            val listener = RecordingListener()
            val scope = WorkGraphCoroutineScope<AbstractSerializableGraphNode>("task", listener)

            graph.executeNodePublic(scope, mapOf(out to "done"), exit)

            assertTrue(listener.finishedLatch.await(2, TimeUnit.SECONDS))
            assertEquals(mapOf("out" to "done"), listener.finishedOutputs.get())

            scope.cancel()
        }

        assertThrows(IllegalStateException::class.java) {
            runBlocking {
                val graph = TestGraph("TestGraph")
                val node = CapturingNode(
                    nodeId = "node",
                    nodeName = "Node",
                    inputs = emptyList(),
                    outputs = emptyList(),
                    resultFactory = { emptyMap() }
                )
                graph.addNode(node)

                val scope = WorkGraphCoroutineScope<AbstractSerializableGraphNode>("task2", null)
                try {
                    graph.executeNodePublic(scope, emptyMap(), node)
                } finally {
                    scope.cancel()
                }
            }
        }
    }

    @Test
    fun start() {
        val graph = WorkFlowGraph("TestGraph")

        val present = GraphNodeParameter(String::class, "present")
        val missing = GraphNodeParameter(String::class, "missing")

        val entry = GraphNodeEntry("entry", "Entry", listOf(present, missing))

        val noEdge = GraphNodeParameter(String::class, "noEdge")
        val needsMissingOutput = GraphNodeParameter(String::class, "needsMissingOutput")
        val needsFuture = GraphNodeParameter(String::class, "needsFuture")
        val needsPresent = GraphNodeParameter(String::class, "needsPresent")

        val midOut = GraphNodeParameter(String::class, "midOut")
        val middle = CapturingNode(
            nodeId = "mid",
            nodeName = "Middle",
            inputs = listOf(noEdge, needsMissingOutput, needsFuture, needsPresent),
            outputs = listOf(midOut),
            resultFactory = { mapOf(midOut to "mid-value") }
        )

        val futureOut = GraphNodeParameter(String::class, "futureOut")
        val future = GraphNodeExit("future", "Future", listOf(futureOut), strict = true)

        val finalOut = GraphNodeParameter(String::class, "final")
        val exit = GraphNodeExit("exit", "Exit", listOf(finalOut), strict = true)

        graph.addNode(entry)
        graph.addNode(middle)
        graph.addNode(future)
        graph.addNode(exit)

        graph.addTriggerEdge("entry", "mid")
        graph.addTriggerEdge("mid", "exit")

        graph.addParameterTransmissionEdge("entry", "mid", "present", "needsPresent")
        graph.addParameterTransmissionEdge("entry", "mid", "missing", "needsMissingOutput")
        graph.addParameterTransmissionEdge("future", "mid", "futureOut", "needsFuture")
        graph.addParameterTransmissionEdge("mid", "exit", "midOut", "final")

        val listener = RecordingListener()
        val taskId = graph.start(mapOf(present to "present-value"), listener)

        assertTrue(listener.startedLatch.await(2, TimeUnit.SECONDS))
        assertEquals(taskId, listener.startedTaskId.get())

        assertTrue(middle.executeLatch.await(2, TimeUnit.SECONDS))
        val middleInputs = middle.receivedInput.get()
        assertEquals("present-value", middleInputs[needsPresent])
        assertNull(middleInputs[needsMissingOutput])
        assertNull(middleInputs[needsFuture])
        assertNull(middleInputs[noEdge])
        assertEquals("present-value", middleInputs[GraphNodeParameter(String::class, "Entry.present")])

        assertTrue(listener.finishedLatch.await(2, TimeUnit.SECONDS))
        assertEquals(taskId, listener.finishedTaskId.get())
        assertEquals(mapOf("final" to "mid-value"), listener.finishedOutputs.get())

        graph.stop(taskId)

        run {
            val graphWithoutListener = WorkFlowGraph("TestGraph")
            val p = GraphNodeParameter(String::class, "p")
            graphWithoutListener.addNode(GraphNodeEntry("entry", "Entry", listOf(p)))
            graphWithoutListener.addNode(GraphNodeExit("exit", "Exit", listOf(p), strict = true))
            graphWithoutListener.addTriggerEdge("entry", "exit")

            val id = graphWithoutListener.start(mapOf(p to "v"), null)
            assertTrue(id.isNotBlank())
            graphWithoutListener.stop(id)
        }
    }

    @Test
    fun stop() {
        val graph = WorkFlowGraph("TestGraph")

        val entry = GraphNodeEntry("entry", "Entry", emptyList())
        val gate = CompletableDeferred<Unit>()
        val middle = CapturingNode(
            nodeId = "mid",
            nodeName = "Middle",
            inputs = emptyList(),
            outputs = emptyList(),
            resultFactory = {
                gate.await()
                emptyMap()
            }
        )

        graph.addNode(entry)
        graph.addNode(middle)
        graph.addTriggerEdge("entry", "mid")

        assertDoesNotThrow { graph.stop("missing") }

        val listener = RecordingListener()
        val taskId = graph.start(emptyMap(), listener)

        runBlocking {
            withTimeout(2_000) {
                while (graph.getTaskExecutor(taskId).runningNodes().isEmpty()) {
                    delay(10)
                }
            }
        }

        graph.stop(taskId)
        assertTrue(listener.cancelledLatch.await(2, TimeUnit.SECONDS))
        assertEquals(taskId, listener.cancelledTaskId.get())
    }

    @Test
    fun awaitTask() {
        runBlocking {
            val graph = WorkFlowGraph("TestGraph")

            withTimeout(200) {
                graph.awaitTask("missing")
            }

            val entry = GraphNodeEntry("entry", "Entry", emptyList())
            val middle = CapturingNode(
                nodeId = "mid",
                nodeName = "Middle",
                inputs = emptyList(),
                outputs = emptyList(),
                delayMillis = 5_000,
                resultFactory = { emptyMap() }
            )

            graph.addNode(entry)
            graph.addNode(middle)
            graph.addTriggerEdge("entry", "mid")

            val taskId = graph.start(emptyMap(), null)

            launch {
                delay(150)
                graph.stop(taskId)
            }

            withTimeout(2_000) {
                graph.awaitTask(taskId)
            }
        }
    }

    @Test
    fun getTaskExecutor() {
        val graph = WorkFlowGraph("TestGraph")

        val p = GraphNodeParameter(String::class, "p")
        graph.addNode(GraphNodeEntry("entry", "Entry", listOf(p)))
        graph.addNode(GraphNodeExit("exit", "Exit", listOf(p), strict = true))
        graph.addTriggerEdge("entry", "exit")

        val taskId = graph.start(mapOf(p to "v"), null)
        val executor = graph.getTaskExecutor(taskId)
        assertEquals(taskId, executor.taskId)

        assertThrows(IllegalArgumentException::class.java) { graph.getTaskExecutor("missing") }

        graph.stop(taskId)
    }

    @Test
    fun clear() {
        val graph = WorkFlowGraph("TestGraph")

        val p = GraphNodeParameter(String::class, "p")
        graph.addNode(GraphNodeEntry("entry", "Entry", listOf(p)))
        graph.addNode(GraphNodeExit("exit", "Exit", listOf(p)))
        graph.addTriggerEdge("entry", "exit")
        graph.addParameterTransmissionEdge("entry", "exit", "p", "p")

        graph.clear()

        assertThrows(IllegalStateException::class.java) { graph.getEntry() }
        assertThrows(IllegalArgumentException::class.java) { graph.queryEdges("entry") }
        assertThrows(IllegalArgumentException::class.java) { graph.addTriggerEdge("entry", "exit") }
    }

    @Test
    fun getNodeById() {
        val graph = TestGraph("TestGraph")

        val p = GraphNodeParameter(String::class, "p")
        val entry = GraphNodeEntry("entry", "Entry", listOf(p))
        graph.addNode(entry)

        assertSame(entry, graph.getNodeByIdPublic("entry"))
        assertThrows(IllegalArgumentException::class.java) { graph.getNodeByIdPublic("missing") }
    }

    @Test
    fun serializeAndLoadFromSerialized() {
        val original = WorkFlowGraph("Original")

        val condition = GraphNodeParameter(Boolean::class, GraphNodeIf.INPUT_CONDITION)
        val entry = GraphNodeEntry("entry", "Entry", listOf(condition), strict = true)
        val ifNode = GraphNodeIf("if", "If")
        val exit = GraphNodeExit("exit", "Exit", listOf(GraphNodeParameter(String::class, "out")), strict = true)

        original.addNode(entry)
        original.addNode(ifNode)
        original.addNode(exit)

        original.addTriggerEdge("entry", "if")
        original.addTriggerEdge("if", "exit", GraphNodeIf.GROUP_PASSED)
        original.addTriggerEdge("if", "exit", GraphNodeIf.GROUP_FAILED)
        original.addParameterTransmissionEdge("entry", "if", GraphNodeIf.INPUT_CONDITION, GraphNodeIf.INPUT_CONDITION)

        val serialized = original.serialize()

        val loaded = WorkFlowGraph("Loaded")
        loaded.loadFromSerialized(serialized)

        assertEquals("entry", loaded.getEntry().nodeId)
        assertEquals(listOf("if"), loaded.queryEdges("entry").triggerTargets[DEFAULT_EDGE_GROUP])
        assertNotNull(loaded.queryEdges("if").parameterOrigins[GraphNodeIf.INPUT_CONDITION])

        assertThrows(IllegalArgumentException::class.java) { loaded.getDeserializer("UNKNOWN") }

        loaded.registerDeserializer(GraphNodeType.ENTRY, GraphNodeDeserializer { data ->
            GraphNodeEntry(
                data["nodeId"] as String,
                "OverriddenEntryName",
                emptyList(),
                strict = false
            )
        })
        val overriddenEntry = loaded.getDeserializer(GraphNodeType.ENTRY.getTypeName()).deserialize(
            mapOf(
                "nodeId" to "id",
                "nodeName" to "ignored",
                "inputs" to emptyList<Map<String, String>>(),
                "strict" to "false"
            )
        )
        assertEquals("OverriddenEntryName", overriddenEntry.nodeName)
    }

    @Test
    fun graphNodeIfRoutesByCondition() {
        fun runGraph(condition: Boolean): Map<String, Any?> {
            val graph = WorkFlowGraph("TestGraph")

            val conditionParam = GraphNodeParameter(Boolean::class, GraphNodeIf.INPUT_CONDITION)
            val passed = GraphNodeParameter(String::class, "passedValue")
            val failed = GraphNodeParameter(String::class, "failedValue")
            val passedResult = GraphNodeParameter(String::class, "passedResult")
            val failedResult = GraphNodeParameter(String::class, "failedResult")

            val entry = GraphNodeEntry("entry", "Entry", listOf(conditionParam, passed, failed))
            val ifNode = GraphNodeIf("if", "If")
            val exitPassed = GraphNodeExit("exitPassed", "ExitPassed", listOf(passedResult), strict = true)
            val exitFailed = GraphNodeExit("exitFailed", "ExitFailed", listOf(failedResult), strict = true)

            graph.addNode(entry)
            graph.addNode(ifNode)
            graph.addNode(exitPassed)
            graph.addNode(exitFailed)

            graph.addTriggerEdge("entry", "if")
            graph.addTriggerEdge("if", "exitPassed", GraphNodeIf.GROUP_PASSED)
            graph.addTriggerEdge("if", "exitFailed", GraphNodeIf.GROUP_FAILED)

            graph.addParameterTransmissionEdge("entry", "if", GraphNodeIf.INPUT_CONDITION, GraphNodeIf.INPUT_CONDITION)
            graph.addParameterTransmissionEdge("entry", "exitPassed", "passedValue", "passedResult")
            graph.addParameterTransmissionEdge("entry", "exitFailed", "failedValue", "failedResult")

            val listener = RecordingListener()
            val taskId = graph.start(
                mapOf(
                    conditionParam to condition,
                    passed to "PASS",
                    failed to "FAIL"
                ),
                listener
            )

            assertTrue(listener.finishedLatch.await(2, TimeUnit.SECONDS))
            graph.stop(taskId)

            return listener.finishedOutputs.get()
        }

        assertEquals(mapOf("passedResult" to "PASS"), runGraph(condition = true))
        assertEquals(mapOf("failedResult" to "FAIL"), runGraph(condition = false))
    }

    @Test
    fun resolveParametersAndReferences() {
        val resolver = ParameterResolvingNode()

        val name = GraphNodeParameter(String::class, "name")
        val user = GraphNodeParameter(Map::class, "user")
        val inputData = mapOf(
            name to "Alice",
            user to mapOf(
                "profile" to mapOf(
                    "age" to 30
                )
            )
        )

        assertEquals("Hello Alice", resolver.resolve(inputData, "Hello {{name}}"))
        assertEquals(30, resolver.resolveRef(inputData, "user.profile.age"))

        assertThrows(IllegalArgumentException::class.java) {
            resolver.resolveRef(inputData, "missing")
        }
    }

    @Test
    fun entryAndExitStrictModes() {
        runBlocking {
            val a = GraphNodeParameter(String::class, "a")
            val extra = GraphNodeParameter(String::class, "extra")
            val input = mapOf(a to "x", extra to "y")

            val entryStrict = GraphNodeEntry("entry", "Entry", listOf(a), strict = true)
            assertEquals(mapOf(a to "x"), entryStrict.execute(input))

            val exitNonStrict = GraphNodeExit("exit", "Exit", listOf(a), strict = false)
            assertEquals(input, exitNonStrict.execute(input))
        }
    }

    @Test
    fun graphNodeParameterSerialization() {
        val p = GraphNodeParameter(String::class, "p")
        val json = Gson().toJson(p.serialize())

        assertEquals(p, GraphNodeParameter.fromSerialized(json))
        assertEquals(p, GraphNodeParameter.fromSerialized(p.serialize()))
    }

    private object TestNodeType : IGraphNodeType {
        override fun getTypeName(): String = "TEST"
    }

    private class TestGraph(graphName: String) : AbstractSerializableWorkFlowGraph(graphName) {
        fun getNodeByIdPublic(nodeId: String): AbstractSerializableGraphNode {
            return getNodeById(nodeId)
        }

        suspend fun executeNodePublic(
            scope: WorkGraphCoroutineScope<AbstractSerializableGraphNode>,
            inputData: Map<GraphNodeParameter, Any?>,
            node: AbstractGraphNode
        ) {
            executeNode(scope, inputData, node)
        }
    }

    private class RecordingListener : WorkFlowGraphListener {
        val startedLatch = CountDownLatch(1)
        val finishedLatch = CountDownLatch(1)
        val cancelledLatch = CountDownLatch(1)

        val startedTaskId = AtomicReference<String?>(null)
        val finishedTaskId = AtomicReference<String?>(null)
        val cancelledTaskId = AtomicReference<String?>(null)
        val finishedOutputs = AtomicReference<Map<String, Any?>>(emptyMap())

        override fun onTaskStarted(taskId: String) {
            startedTaskId.set(taskId)
            startedLatch.countDown()
        }

        override fun onTaskFinished(taskId: String, outputs: Map<String, Any?>) {
            finishedTaskId.set(taskId)
            finishedOutputs.set(outputs)
            finishedLatch.countDown()
        }

        override fun onTaskCancelled(taskId: String) {
            cancelledTaskId.set(taskId)
            cancelledLatch.countDown()
        }
    }

    private class CapturingNode(
        nodeId: String,
        nodeName: String,
        inputs: List<GraphNodeParameter>,
        outputs: List<GraphNodeParameter>,
        private val delayMillis: Long = 0,
        private val resultFactory: suspend (Map<GraphNodeParameter, Any?>) -> Map<GraphNodeParameter, Any?>
    ) : AbstractSerializableGraphNode(TestNodeType, nodeId, nodeName, inputs, outputs) {
        val receivedInput = AtomicReference<Map<GraphNodeParameter, Any?>>(emptyMap())
        val executeLatch = CountDownLatch(1)

        override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
            receivedInput.set(inputData)
            executeLatch.countDown()

            if (delayMillis > 0) {
                delay(delayMillis)
            }

            return resultFactory.invoke(inputData)
        }
    }

    private class ParameterResolvingNode : AbstractGraphNode(
        nodeType = TestNodeType,
        nodeId = "resolver",
        nodeName = "Resolver",
        inputs = emptyList(),
        outputs = emptyList()
    ) {
        override suspend fun execute(inputData: Map<GraphNodeParameter, Any?>): Map<GraphNodeParameter, Any?> {
            return emptyMap()
        }

        fun resolve(inputData: Map<GraphNodeParameter, Any?>, text: String): String {
            return resolveParameters(inputData, text)
        }

        fun resolveRef(inputData: Map<GraphNodeParameter, Any?>, ref: String): Any? {
            return resolveParameterReference(inputData, ref)
        }
    }
}
