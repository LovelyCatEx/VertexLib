package com.lovelycatv.vertex.work.worker

import com.lovelycatv.vertex.work.data.InputWorkDataMerger
import com.lovelycatv.vertex.work.data.OverridingInputDataMerger
import java.util.UUID
import kotlin.math.max

/**
 * @author lovelycat
 * @since 2024-10-27 21:31
 * @version 1.0
 */
class WorkChain(val blocks: List<Block>) {
    val chainId = UUID.randomUUID().toString()

    fun getWorksCount(): Int = getAllWorks().size

    fun getAllWorks() = blocks.flatMap { it.works }

    sealed class Block(
        val type: Type,
        val works: List<WrappedWork>
    ) {
        enum class Type {
            PARALLEL,
            PARALLEL_INBOUND,
            SEQUENCE
        }
    }

    class SequenceBlock(works: List<WrappedWork>) : Block(Type.SEQUENCE, works)

    class ParallelBlock(works: List<WrappedWork>) : Block(Type.PARALLEL, works)

    class ParallelInboundBlock(works: List<WrappedWork>, val inputWorkDataMerger: InputWorkDataMerger) : Block(Type.PARALLEL_INBOUND, works)

    class Builder {
        private val blocks = mutableListOf<Block>()

        fun sequence(vararg works: WrappedWork): Builder {
            blocks.add(SequenceBlock(works = works.toList()))
            return this
        }

        fun parallel(vararg works: WrappedWork): Builder {
            blocks.add(ParallelBlock(works = works.toList()))
            return this
        }

        fun parallelInBound(vararg works: WrappedWork): Builder {
            blocks.add(ParallelInboundBlock(works = works.toList(), inputWorkDataMerger = OverridingInputDataMerger()))
            return this
        }

        fun transmit(inputWorkDataMerger: InputWorkDataMerger): Builder {
            val lastBlock = blocks[blocks.lastIndex]

            // Parallel Block is not supported
            if (lastBlock.type == Block.Type.PARALLEL) {
                throw IllegalStateException("Parallel Block does not support transmitting data to next Block")
            }

            if (lastBlock.type == Block.Type.PARALLEL_INBOUND) {
                blocks[blocks.lastIndex] = (lastBlock as ParallelInboundBlock).run {
                    ParallelInboundBlock(this.works, inputWorkDataMerger)
                }
            }

            return this
        }

        fun build(): WorkChain {
            return WorkChain(blocks)
        }
    }

    fun inspect(): String {
        val blockTextsList = mutableListOf<MutableList<String>>()
        blockTextsList.add(mutableListOf<String>().apply {
            this.add("ChainId: ${this@WorkChain.chainId}")
            this.add("Blocks: ${this@WorkChain.blocks.size}")
        })
        for (block in blocks) {
            val fxGetWorkDisplayPrefix = fun (workIndex: Int): String {
                return when (block.type) {
                    Block.Type.SEQUENCE -> {
                        if (workIndex == 0) "*" else "+"
                    }
                    Block.Type.PARALLEL -> {
                        "*"
                    }
                    Block.Type.PARALLEL_INBOUND -> {
                        "*"
                    }
                }
            }

            val textList = mutableListOf<String>()
            textList.add("${block.type}")
            if (block is ParallelInboundBlock) {
                textList.add("  > workResultDataMerger: ${block.inputWorkDataMerger::class.qualifiedName}")
            }
            block.works.forEachIndexed { index, work ->
                textList.add("${fxGetWorkDisplayPrefix.invoke(index)} ${work.getWorker().workName} # ${work.getWorker()::class.qualifiedName}")
                textList.add("  > id: ${work.getWorkerId()}")
                textList.add("  > retryStrategy: ${work.getRetryStrategy().type}, maxRetryTimes: ${work.getRetryStrategy().maxRetryTimes}")
                textList.add("  > failureStrategy: ${work.getFailureStrategy()}")
                if (work.getWorker().inputData.isNotEmpty()) {
                    val inputData = work.getWorker().inputData.toString().split("\n")
                    textList.add("  > parameters: ${inputData[0]}")
                    textList.addAll(inputData.drop(1).map { " ".repeat(2) + it })
                } else {
                    textList.add("  > parameters: {}")
                }
            }
            blockTextsList.add(textList)

        }

        val finalTextList = mutableListOf<String>()

        // Find max width of the whole text lines
        var maxContentWidth = 0
        blockTextsList.forEach { blockTextList ->
            blockTextList.forEach {
                val length = it.length
                if (length > maxContentWidth) {
                    maxContentWidth = length
                }
            }
        }

        for (i in blockTextsList.indices) {
            val list = blockTextsList[i]
            if (i == 0) {
                finalTextList.add("╔${"═".repeat(maxContentWidth + 2)}╗")
            } else {
                finalTextList.add("╠${"═".repeat(maxContentWidth + 2)}╣")
            }

            finalTextList.addAll(list.map { "║ ${it.padEnd(maxContentWidth, ' ')} ║" })

            if (i == blockTextsList.size - 1) {
                finalTextList.add("╚${"═".repeat(maxContentWidth + 2)}╝")
            }
        }

        return finalTextList.joinToString(separator = "\n", prefix = "", postfix = "")
    }
}