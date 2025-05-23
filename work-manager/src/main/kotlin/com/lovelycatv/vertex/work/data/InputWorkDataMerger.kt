package com.lovelycatv.vertex.work.data

/**
 * @author lovelycat
 * @since 2024-10-31 21:40
 * @version 1.0
 */
interface InputWorkDataMerger {
    fun merge(results: List<WorkData>): WorkData
}