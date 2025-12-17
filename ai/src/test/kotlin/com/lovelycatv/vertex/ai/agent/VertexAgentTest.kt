package com.lovelycatv.vertex.ai.agent

import com.lovelycatv.vertex.ai.openai.ModelProviderBaseUrl
import com.lovelycatv.vertex.ai.openai.VertexAIClient
import com.lovelycatv.vertex.ai.openai.VertexAIClientConfig
import com.lovelycatv.vertex.ai.agent.graph.GraphNodeLLM
import com.lovelycatv.vertex.ai.agent.graph.GraphNodeLLMDeserializer
import com.lovelycatv.vertex.ai.agent.graph.VertexAgentGraphNodeType
import com.lovelycatv.vertex.workflow.graph.WorkFlowGraphListener
import com.lovelycatv.vertex.workflow.graph.node.GraphNodeEntry
import com.lovelycatv.vertex.workflow.graph.node.GraphNodeExit
import com.lovelycatv.vertex.workflow.graph.node.GraphNodeParameter
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VertexAgentTest {
    private val aiClientDeepseek = VertexAIClient(
        VertexAIClientConfig(
            baseUrl = ModelProviderBaseUrl.DEEPSEEK,
            apiKey = System.getProperty("VertexAIClientTestDeepSeekApiKey"),
            enableLogging = true
        )
    )

    private val agent = VertexAgent<String>("test-agent", "TestAgent")

    @Test
    fun composeWorkFlowGraph() {
        agent.composeWorkFlowGraph {
            accessGraph {
                clear()
                val nodeEntry = GraphNodeEntry(
                    nodeName = "Entry",
                    inputs = listOf(
                        GraphNodeParameter(
                            String::class,
                            "userInput"
                        ),
                        GraphNodeParameter(
                            String::class,
                            "model"
                        )
                    )
                )
                val node1 = GraphNodeLLM(nodeName = "LLM", vertexAIClient = aiClientDeepseek)
                val nodeExit = GraphNodeExit(
                    nodeName = "Exit",
                    outputValueType = String::class,
                    strict = true
                )

                addNode(nodeEntry)
                addNode(node1)
                addNode(nodeExit)

                addTriggerEdge(nodeEntry.nodeId, node1.nodeId)
                addTriggerEdge(node1.nodeId, nodeExit.nodeId)

                addParameterTransmissionEdge(nodeEntry.nodeId, node1.nodeId, "userInput", GraphNodeLLM.INPUT_USER_PROMPT)
                addParameterTransmissionEdge(nodeEntry.nodeId, node1.nodeId, "model", GraphNodeLLM.INPUT_MODEL)
                addParameterTransmissionEdge(node1.nodeId, nodeExit.nodeId, GraphNodeLLM.OUTPUT_CONTENT, GraphNodeLLM.OUTPUT_CONTENT)

                val s = serialize()
                registerDeserializer(VertexAgentGraphNodeType.LLM, GraphNodeLLMDeserializer())
                loadFromSerialized(s)
            }
        }
    }

    @Test
    fun start() {
        composeWorkFlowGraph()

        runBlocking {
            val result = suspendCoroutine {
                agent.start(
                    mutableMapOf(
                        "userInput" to (String::class to "Hello! If you can hear me, just reply character 1 only."),
                        "model" to (String::class to "deepseek-chat"),
                    ),
                    object : WorkFlowGraphListener<String> {
                        override fun onTaskStarted(taskId: String) {
                            println("onTaskStarted $taskId")
                        }

                        override fun onTaskFinished(taskId: String, outputData: String?) {
                            println("onTaskFinished $taskId")
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
        composeWorkFlowGraph()

        runBlocking {
            val result = suspendCoroutine {
                val taskId = agent.start(
                    mutableMapOf(
                        "userInput" to (String::class to "Hello! introduce yourself please!"),
                        "model" to (String::class to "deepseek-chat"),
                    ),
                    object : WorkFlowGraphListener<String> {
                        override fun onTaskStarted(taskId: String) {
                            println("onTaskStarted $taskId")
                        }

                        override fun onTaskFinished(taskId: String, outputData: String?) {
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