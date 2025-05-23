package com.lovelycatv.vertex.work

import com.lovelycatv.vertex.coroutines.runCoroutineAsync
import com.lovelycatv.vertex.work.data.WorkData
import com.lovelycatv.vertex.work.data.WorkResult
import com.lovelycatv.vertex.work.exception.DuplicateWorkChainException
import com.lovelycatv.vertex.work.interceptor.AbstractWorkChainInterceptor
import com.lovelycatv.vertex.work.scope.StartedWorkChain
import com.lovelycatv.vertex.work.scope.WorkChainCoroutineScope
import com.lovelycatv.vertex.work.scope.WorkCoroutineScope
import com.lovelycatv.vertex.work.scope.WorkExceptionHandler
import com.lovelycatv.vertex.work.worker.WorkChain
import com.lovelycatv.vertex.work.worker.WrappedWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext

/**
 * @author lovelycat
 * @since 2025-05-24 01:12
 * @version 1.0
 */
abstract class AbstractWorkManager {
    protected val startedChains = mutableMapOf<String, StartedWorkChain>()

    fun getRunningChains(): List<StartedWorkChain> {
        return this.startedChains.filter { it.value.isRunning() }.values.toList()
    }

    open fun requireEmptyWorkCoroutineScope(
        context: CoroutineContext,
        exceptionHandler: WorkExceptionHandler?
    ): WorkCoroutineScope {
        return WorkCoroutineScope(context, exceptionHandler)
    }

    fun runWorkChain(
        workChain: WorkChain,
        coroutineContext: CoroutineContext = Dispatchers.IO,
        interceptor: AbstractWorkChainInterceptor? = null
    ): StartedWorkChain {
        // Validate WorkChain
        val workChainStartedBefore = startedChains.containsKey(workChain.chainId)

        if (workChainStartedBefore) {
            throw DuplicateWorkChainException(workChain.chainId)
        }

        // Find a empty WorkCoroutineScope
        val workCoroutineScope = requireEmptyWorkCoroutineScope(coroutineContext, object : WorkExceptionHandler {
            override fun invoke(worker: WrappedWork, e: Exception) {
                interceptor?.onException(worker, e)
            }
        })

        workCoroutineScope.initialize(workChain.getWorksCount())

        val scope = WorkChainCoroutineScope()

        val deferred = runCoroutineAsync(scope) {
            var outputsFromLastBlock: WorkData? = null
            val fxGetOutputsFromLastBlock = fun (): WorkData {
                return outputsFromLastBlock ?: WorkData.build()
            }
            blockLoop@ for ((blockIndex, block) in workChain.blocks.withIndex()) {
                interceptor?.beforeBlockStarted(blockIndex, block)
                if (block.type == WorkChain.Block.Type.SEQUENCE) {
                    // In sequence
                    var outputsFromLastSequenceWork: WorkData? = null
                    for ((workIndex, work) in block.works.withIndex()) {
                        interceptor?.beforeWorkStarted(blockIndex, block, work)

                        // The outputs from last block should be transmitted to the first work of sequence
                        val inputData = if (workIndex == 0)
                            fxGetOutputsFromLastBlock()
                        else
                            outputsFromLastSequenceWork ?: WorkData.build()

                        val finalResult = runWorkWithRetry(workCoroutineScope, work, inputData)

                        if (!finalResult.isCompletedOrStopped()) {
                            if (work.getFailureStrategy() == WorkFailureStrategy.INTERRUPT_BLOCK) {
                                interceptor?.onBlockInterrupted(
                                    blockIndex = blockIndex,
                                    block = block,
                                    producer = work
                                )
                                // The sequence block is end up with failure and the outputs of this block should be null
                                outputsFromLastSequenceWork = null
                                break
                            } else if (work.getFailureStrategy() == WorkFailureStrategy.INTERRUPT_CHAIN) {
                                interceptor?.onChainInterrupted(
                                    blockIndex = blockIndex,
                                    block = block,
                                    producer = work
                                )
                                // The work interrupt the chain so the final result should be null
                                outputsFromLastBlock = null
                                break@blockLoop
                            }
                        }

                        outputsFromLastSequenceWork = finalResult.output
                    }
                    // Recognize the outputs from the last work in the sequence as the outputs of this block
                    outputsFromLastBlock = outputsFromLastSequenceWork
                } else {
                    val fxGetFailedWorkIndexes = fun (results: List<WorkResult>): List<Int> {
                        return results.mapIndexedNotNull { index, it -> if (!it.isCompletedOrStopped()) index else null }
                    }

                    if (block.type == WorkChain.Block.Type.PARALLEL_INBOUND) {
                        // Parallel in bound
                        val deferred = block.works.map { work ->
                            interceptor?.beforeWorkStarted(blockIndex, block, work)
                            workCoroutineScope.launchTaskAsync(work, fxGetOutputsFromLastBlock())
                        }

                        val results = deferred.awaitAll()
                        // Check and retry failure works
                        val failedWorkIndexes = fxGetFailedWorkIndexes(results)
                        val failedWorks = failedWorkIndexes
                            .map { block.works[it] }
                            .map {
                                // Retry all failed works
                                runCoroutineAsync {
                                    runWorkWithRetry(workCoroutineScope, it, fxGetOutputsFromLastBlock())
                                }
                            }

                        val resultsOfFailureWorks = failedWorks.awaitAll()
                        val indexesOfWorksStillFailed = fxGetFailedWorkIndexes(resultsOfFailureWorks)

                        // Check again and determine whether to do next
                        val finalFailedStrategies = indexesOfWorksStillFailed.map { block.works[it] }.map { it.getFailureStrategy() }
                        if (finalFailedStrategies.contains(WorkFailureStrategy.INTERRUPT_CHAIN)) {
                            interceptor?.onChainInterrupted(
                                blockIndex = blockIndex,
                                block = block,
                                producer = block.works[finalFailedStrategies.indexOf(WorkFailureStrategy.INTERRUPT_CHAIN)]
                            )
                            // A work interrupt the chain so the final result should be null
                            outputsFromLastBlock = null
                            break@blockLoop
                        } else if (finalFailedStrategies.contains(WorkFailureStrategy.INTERRUPT_BLOCK)) {
                            interceptor?.onBlockInterrupted(
                                blockIndex = blockIndex,
                                block = block,
                                producer = block.works[finalFailedStrategies.indexOf(WorkFailureStrategy.INTERRUPT_BLOCK)]
                            )
                            // The parallel in bound block is end up with failure and the outputs of this block should be null
                            outputsFromLastBlock = null
                            continue@blockLoop
                        }

                        // Get the input data merger
                        val merger = block.inputWorkDataMerger
                        // The first batch of successful works
                        val a = results.filterIndexed { index, _ -> index !in failedWorkIndexes }.map { it.output }
                        // The second batch of successful works after retry
                        val b = resultsOfFailureWorks.filterIndexed { index, _ -> index !in indexesOfWorksStillFailed }.map { it.output }
                        // Save the final outputs
                        outputsFromLastBlock = merger.merge(a + b)
                    } else {
                        // Parallel
                        block.works.forEach { work ->
                            interceptor?.beforeWorkStarted(blockIndex, block, work)
                            workCoroutineScope.launchTask(work, fxGetOutputsFromLastBlock())
                        }
                        // Parallel Block does not support transmitting data to next block
                        outputsFromLastBlock = null
                    }
                }
                interceptor?.afterBlockFinished(blockIndex, block)
            }
            outputsFromLastBlock
        }

        val startedWorkChain = StartedWorkChain(
            originalWorkChain = workChain,
            chainCoroutineScope = scope,
            workCoroutineScope = workCoroutineScope,
            workChainResult = deferred
        )

        this.startedChains[workChain.chainId] = startedWorkChain
        return startedWorkChain
    }

    protected suspend fun runWorkWithRetry(workScope: WorkCoroutineScope, work: WrappedWork, inputData: WorkData): WorkResult {
        var result: WorkResult? = null
        var retriedCount = 1
        val maxRetryTimes = work.getRetryStrategy().maxRetryTimes
        if (work.getRetryStrategy().type == WorkRetryStrategy.Type.RETRY) {
            while (result == null || (!result.isCompletedOrStopped() && retriedCount <= maxRetryTimes)) {
                delay(work.getRetryStrategy().retryInterval.invoke(retriedCount))
                result = workScope.launchTaskAsync(work, inputData).await()
                retriedCount++
            }
        } else {
            result = workScope.launchTaskAsync(work, inputData).await()
        }
        return result
    }
}