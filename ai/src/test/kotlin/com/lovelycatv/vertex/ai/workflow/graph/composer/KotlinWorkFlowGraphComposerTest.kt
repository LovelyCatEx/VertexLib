package com.lovelycatv.vertex.ai.workflow.graph.composer

import com.lovelycatv.vertex.ai.workflow.graph.WorkFlowGraph
import com.lovelycatv.vertex.ai.workflow.graph.WorkFlowGraphListener
import com.lovelycatv.vertex.ai.workflow.graph.node.GraphNodeParameter
import com.lovelycatv.vertex.ai.workflow.graph.node.math.GraphNodeAdd
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.test.assertEquals

class KotlinWorkFlowGraphComposerTest {
    @Test
    fun triggerAndTransmit() {
        val composer = WorkFlowGraphComposer(
            graphFactory = {
                WorkFlowGraph("TestGraph")
            }
        )

        with(composer) {
            val entry = entry {
                addInputParameter(Int::class, "x")
                addInputParameter(Int::class, "y")
            }

            val add = add()

            val exit = exit {
                addOutputParameter(Int::class, "z")
            }

            entry transmit "x" to GraphNodeAdd.INPUT_X inNode add
            entry transmit "y" to GraphNodeAdd.INPUT_Y inNode add

            add transmit "z" to GraphNodeAdd.OUTPUT_Z inNode exit

            entry.trigger(add)
            add.trigger(exit)
        }

        val graph = composer.build()

        val x = 2
        val y = 5
        val z = x + y

        val result = runBlocking {
            suspendCoroutine {
                graph.start(
                    mapOf(
                        GraphNodeParameter(Int::class, "x") to x,
                        GraphNodeParameter(Int::class, "y") to y
                    ),
                    object : WorkFlowGraphListener {
                        override fun onTaskStarted(taskId: String) {
                        }

                        override fun onTaskFinished(
                            taskId: String,
                            outputs: Map<String, Any?>
                        ) {
                            it.resume(outputs["z"]!! as Int)
                        }

                        override fun onTaskCancelled(taskId: String) {}

                    }
                )
            }
        }

        assertEquals(z, result)
    }

}