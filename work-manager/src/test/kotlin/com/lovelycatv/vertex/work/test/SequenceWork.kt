package com.lovelycatv.vertex.work.test

import com.lovelycatv.vertex.work.data.WorkResult
import com.lovelycatv.vertex.work.base.AbstractWork
import com.lovelycatv.vertex.work.data.WorkData
import kotlinx.coroutines.delay

/**
 * @author lovelycat
 * @since 2024-11-01 13:48
 * @version 1.0
 */
class SequenceWork(workName: String, inputData: WorkData) : AbstractWork(workName, inputData) {
    override suspend fun doWork(preBlockOutputData: WorkData): WorkResult {
        println("$workName received params: ${preBlockOutputData.toPairList()}")
        runInProtected {
            for (i in 0..2) {
                delay(500)
                println("Sequence Work: $workName -> $i")
            }
        }
        return WorkResult.completed(preBlockOutputData + WorkData.build(workName to (500..1000).random()))
    }
}