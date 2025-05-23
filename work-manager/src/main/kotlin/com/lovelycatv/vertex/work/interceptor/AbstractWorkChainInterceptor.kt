package com.lovelycatv.vertex.work.interceptor

import com.lovelycatv.vertex.work.worker.WorkChain
import com.lovelycatv.vertex.work.worker.WrappedWork

/**
 * @author lovelycat
 * @since 2024-10-27 23:36
 * @version 1.0
 */
abstract class AbstractWorkChainInterceptor {
    abstract fun beforeBlockStarted(blockIndex: Int, block: WorkChain.Block)

    abstract fun afterBlockFinished(blockIndex: Int, block: WorkChain.Block)

    abstract fun beforeWorkStarted(blockIndex: Int, block: WorkChain.Block, work: WrappedWork)

    abstract fun onBlockInterrupted(blockIndex: Int, block: WorkChain.Block, producer: WrappedWork)

    abstract fun onChainInterrupted(blockIndex: Int, block: WorkChain.Block, producer: WrappedWork)

    abstract fun onException(producer: WrappedWork, e: Exception)
}