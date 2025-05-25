package com.lovelycatv.vertex.work.interceptor

import com.lovelycatv.vertex.log.logger
import com.lovelycatv.vertex.work.worker.WorkChain
import com.lovelycatv.vertex.work.worker.WrappedWork

/**
 * @author lovelycat
 * @since 2024-10-31 21:17
 * @version 1.0
 */
open class DefaultWorkChainInterceptor : AbstractWorkChainInterceptor() {
    private val logger = logger()

    override fun beforeBlockStarted(blockIndex: Int, block: WorkChain.Block) {}

    override fun afterBlockFinished(blockIndex: Int, block: WorkChain.Block) {}

    override fun beforeWorkStarted(blockIndex: Int, block: WorkChain.Block, work: WrappedWork) {}

    override fun onBlockInterrupted(blockIndex: Int, block: WorkChain.Block, producer: WrappedWork) {}

    override fun onChainInterrupted(blockIndex: Int, block: WorkChain.Block, producer: WrappedWork) {}

    override fun onException(producer: WrappedWork, e: Exception) {
        logger.warn("Work [${producer.getWork().workName} # ${producer.getWorkId()}] produced an exception:")
        logger.warn(producer.getWork().getCurrentWorkResult().toString(), e)
    }
}