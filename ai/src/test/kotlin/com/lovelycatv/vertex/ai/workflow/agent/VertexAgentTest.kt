package com.lovelycatv.vertex.ai.workflow.agent

import com.lovelycatv.vertex.ai.openai.ModelProviderBaseUrl
import com.lovelycatv.vertex.ai.openai.VertexAIClient
import com.lovelycatv.vertex.ai.workflow.agent.graph.GraphNodeLLM
import com.lovelycatv.vertex.ai.workflow.graph.WorkFlowGraphListener
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeEntry
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeExit
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VertexAgentTest {
    private val aiClientDeepseek = VertexAIClient(
        baseUrl = ModelProviderBaseUrl.DEEPSEEK,
        apiKey = System.getProperty("VertexAIClientTestDeepSeekApiKey"),
        enableLogging = true
    )

    private val agent = VertexAgent("test-agent", "TestAgent")

    @Test
    fun accessWorkFlowGraph() {
        agent.accessWorkFlowGraph {
            clear()
            val nodeEntry = GraphNodeEntry(nodeName = "Entry", inputs = listOf())
            val node1 = GraphNodeLLM(nodeName = "LLM", vertexAIClient = aiClientDeepseek, model = "deepseek-chat")
            val nodeExit = GraphNodeExit(
                nodeName = "Exit",
                outputs = listOf(
                    GraphNodeParameter(
                        String::class,
                        GraphNodeLLM.OUTPUT_CONTENT
                    )
                ),
                strict = true
            )

            addNode(nodeEntry)
            addNode(node1)
            addNode(nodeExit)

            addTriggerEdge(nodeEntry.nodeId, node1.nodeId)
            addTriggerEdge(node1.nodeId, nodeExit.nodeId)

            addParameterTransmissionEdge(nodeEntry.nodeId, node1.nodeId, "userInput", GraphNodeLLM.INPUT_USER_PROMPT)
            addParameterTransmissionEdge(node1.nodeId, nodeExit.nodeId, GraphNodeLLM.OUTPUT_CONTENT, GraphNodeLLM.OUTPUT_CONTENT)
        }
    }

    @Test
    fun start() {
        accessWorkFlowGraph()

        runBlocking {
            val result = suspendCoroutine {
                agent.start(
                    mutableMapOf("userInput" to (String::class to "Hello! If you can hear me, just reply character 1 only.")),
                    object : WorkFlowGraphListener {
                        override fun onTaskStarted(taskId: String) {
                            println("onTaskStarted $taskId")
                        }

                        override fun onTaskFinished(
                            taskId: String,
                            outputs: Map<String, Any?>
                        ) {
                            println("onTaskFinished $taskId")
                            println(outputs.toList().joinToString())
                            it.resume(true)
                        }

                        override fun onTaskCancelled(taskId: String) {
                            println("onTaskCancelled $taskId")
                        }

                    }
                )
            }

            assertTrue(result)
        }
    }

    @Test
    fun stop() {
        accessWorkFlowGraph()

        runBlocking {
            val result = suspendCoroutine {
                val taskId = agent.start(
                    mutableMapOf("userInput" to (String::class to "Hello! introduce yourself please!")),
                    object : WorkFlowGraphListener {
                        override fun onTaskStarted(taskId: String) {
                            println("onTaskStarted $taskId")
                        }

                        override fun onTaskFinished(
                            taskId: String,
                            outputs: Map<String, Any?>
                        ) {
                            println("onTaskFinished $taskId")
                        }

                        override fun onTaskCancelled(taskId: String) {
                            println("onTaskCancelled $taskId")

                            it.resume(true)
                        }

                    }
                )
                Thread.sleep(1000)
                agent.stop(taskId)
            }

            assertTrue(result)
        }
    }

}