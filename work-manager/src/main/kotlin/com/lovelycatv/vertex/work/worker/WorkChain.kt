package com.lovelycatv.vertex.work.worker

import com.lovelycatv.vertex.work.data.InputWorkDataMerger
import com.lovelycatv.vertex.work.data.OverridingInputDataMerger
import java.util.UUID

/**
 * @author lovelycat
 * @since 2024-10-27 21:31
 * @version 1.0
 */
class WorkChain(val blocks: List<Block>) {
    val chainId = UUID.randomUUID().toString()

    fun getWorksCount(): Int = getAllWorks().size

    fun getAllWorks() = blocks.flatMap { it.works }

    data class Block(
        val type: Type,
        val works: List<WrappedWork>,
        val inputWorkDataMerger: InputWorkDataMerger = OverridingInputDataMerger()
    ) {
        enum class Type {
            PARALLEL,
            PARALLEL_INBOUND,
            SEQUENCE
        }
    }

    class Builder {
        private val blocks = mutableListOf<Block>()

        fun sequence(vararg works: WrappedWork): Builder {
            blocks.add(Block(type = Block.Type.SEQUENCE, works = works.toList()))
            return this
        }

        fun parallel(vararg works: WrappedWork): Builder {
            blocks.add(Block(type = Block.Type.PARALLEL, works = works.toList()))
            return this
        }

        fun parallelInBound(vararg works: WrappedWork): Builder {
            blocks.add(Block(type = Block.Type.PARALLEL_INBOUND, works = works.toList()))
            return this
        }

        fun transmit(inputWorkDataMerger: InputWorkDataMerger): Builder {
            val lastBlock = blocks[blocks.lastIndex]

            // Parallel Block is not supported
            if (lastBlock.type == Block.Type.PARALLEL) {
                throw IllegalStateException("Parallel Block does not support transmitting data to next Block")
            }

            blocks[blocks.lastIndex] = lastBlock.copy(
                inputWorkDataMerger = inputWorkDataMerger
            )

            return this
        }

        fun build(): WorkChain {
            return WorkChain(blocks)
        }
    }
}