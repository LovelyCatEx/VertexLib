package com.lovelycatv.vertex.work.test

import com.lovelycatv.vertex.work.data.WorkResult
import com.lovelycatv.vertex.work.base.AbstractWork
import com.lovelycatv.vertex.work.data.WorkData

/**
 * @author lovelycat
 * @since 2024-11-01 13:48
 * @version 1.0
 */
class ParallelWork(workName: String, inputData: WorkData) : AbstractWork(workName, inputData) {
    override suspend fun doWork(preBlockOutputData: WorkData): WorkResult {
        println("Parallel Work: $workName")
        return WorkResult.completed()
    }
}