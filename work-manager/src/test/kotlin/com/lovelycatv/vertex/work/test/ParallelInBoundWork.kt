package com.lovelycatv.vertex.work.test

import com.lovelycatv.vertex.work.data.WorkResult
import com.lovelycatv.vertex.work.base.AbstractWork
import com.lovelycatv.vertex.work.data.WorkData

/**
 * @author lovelycat
 * @since 2024-11-01 13:48
 * @version 1.0
 */
class ParallelInBoundWork(workName: String, inputData: WorkData) : AbstractWork(workName, inputData) {
    override suspend fun doWork(preBlockOutputData: WorkData): WorkResult {
        println("Parallel Inbound Work: $workName")
        return WorkResult.completed(WorkData.build("p1" to (0..100).random(), "p2" to (0..100).random()))
    }
}