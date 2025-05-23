package com.lovelycatv.vertex.work.test

import com.lovelycatv.vertex.work.WorkManager
import com.lovelycatv.vertex.work.data.KeyValueMergedInputDataMerger
import com.lovelycatv.vertex.work.extension.WorkBuilder
import com.lovelycatv.vertex.work.worker.WorkChain
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

/**
 * @author lovelycat
 * @since 2024-10-31 14:35
 */
class WorkManagerTest {
    @Test
    fun test() {
        val workManager = WorkManager()

        val p1 = WorkBuilder<ParallelWork>().workName("p1").build()
        val p2 = WorkBuilder<ParallelWork>().workName("p2").build()
        val p3 = WorkBuilder<ParallelWork>().workName("p3").build()

        val pi1 = WorkBuilder<ParallelInBoundWork>().workName("pi1").build()
        val pi2 = WorkBuilder<ParallelInBoundWork>().workName("pi2").build()

        val s1 = WorkBuilder<SequenceWork>().workName("s1").build()
        val s2 = WorkBuilder<SequenceWork>().workName("s2").build()
        val s3 = WorkBuilder<SequenceWork>().workName("s3").build()

        val chainA = WorkChain.Builder()
            .parallel(p1, p2, p3)
            .parallelInBound(pi1, pi2)
            .transmit(KeyValueMergedInputDataMerger())
            .sequence(s1, s2, s3)
            .build()

        runBlocking {
            workManager.runWorkChain(chainA).await()
        }
    }
}