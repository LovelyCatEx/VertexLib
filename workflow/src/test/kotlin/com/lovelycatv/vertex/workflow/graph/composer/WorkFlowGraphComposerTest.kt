package com.lovelycatv.vertex.workflow.graph.composer

import com.lovelycatv.vertex.workflow.graph.WorkFlowGraph
import com.lovelycatv.vertex.workflow.graph.WorkFlowGraphListener
import com.lovelycatv.vertex.workflow.graph.node.GraphNodeParameter
import com.lovelycatv.vertex.workflow.graph.node.math.GraphNodeAdd
import com.lovelycatv.vertex.workflow.graph.node.math.GraphNodeDiv
import com.lovelycatv.vertex.workflow.graph.node.math.GraphNodeMul
import com.lovelycatv.vertex.workflow.graph.node.math.GraphNodeNumberComparator
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.test.assertEquals

class WorkFlowGraphComposerTest {
    @Test
    fun triggerAndTransmit() {
        val composer = WorkFlowGraphComposer(
            graphFactory = {
                WorkFlowGraph<Number>("TestGraph")
            }
        )

        with(composer) {
            val entry = entry {
                addInputParameter(Int::class, "x")
                addInputParameter(Int::class, "y")
            }

            val add = add()

            val numberComparator = compareNumber(type = GraphNodeNumberComparator.Type.GT)

            val ifCond = ifCondition()

            val mul = multiply()
            val div = divide()

            val exit1 = exit(outputType = Number::class)
            val exit2 = exit(outputType = Number::class)

            entry.trigger(add)
            entry transmit "x" to GraphNodeAdd.INPUT_X inNode add
            entry transmit "y" to GraphNodeAdd.INPUT_Y inNode add

            // Compare whether x + y > x
            add.trigger(numberComparator)
            add transmit GraphNodeAdd.OUTPUT_Z to GraphNodeNumberComparator.INPUT_X inNode numberComparator
            entry transmit "x" to GraphNodeNumberComparator.INPUT_Y inNode numberComparator

            numberComparator.trigger(ifCond)
            numberComparator transmitTo ifCond

            entry transmit "x" to GraphNodeMul.INPUT_X inNode mul
            entry transmit "y" to GraphNodeMul.INPUT_Y inNode mul

            entry transmit "x" to GraphNodeDiv.INPUT_X inNode div
            entry transmit "y" to GraphNodeDiv.INPUT_Y inNode div

            ifCond.triggerIfTrue(mul)
            ifCond.triggerIfFalse(div)

            mul.trigger(exit1)
            mul transmitTo exit1

            div.trigger(exit2)
            div transmitTo exit2
        }

        val graph = composer.build()

        println(graph.serialize())

        val x = 2
        val y = -0.5
        val z = -4.0

        val result = runBlocking {
            suspendCoroutine {
                graph.start(
                    mapOf(
                        GraphNodeParameter(Int::class, "x") to x,
                        GraphNodeParameter(Int::class, "y") to y
                    ),
                    object : WorkFlowGraphListener<Number> {
                        override fun onTaskStarted(taskId: String) {
                        }

                        override fun onTaskFinished(taskId: String, outputData: Number?) {
                            it.resume(outputData)
                        }

                        override fun onTaskCancelled(taskId: String) {}

                    }
                )
            }
        }

        assertEquals(z, result)
    }

}