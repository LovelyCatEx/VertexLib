package com.lovelycatv.vertex.work.data

import com.lovelycatv.vertex.map.AnyMap

/**
 * @author lovelycat
 * @since 2024-10-27 20:22
 * @version 1.0
 */
class WorkData : AnyMap<String>() {
    operator fun plus(preBlockOutputData: WorkData): WorkData {
        return build(*(this.toPairList() + preBlockOutputData.toPairList()).toTypedArray())
    }

    fun toPairList(): List<Pair<String, Any?>> {
        return this.map { it.key to it.value }
    }

    companion object {
        fun build(vararg pairs: Pair<String, Any?>): WorkData {
            val workData = WorkData()
            workData.putAll(pairs)
            return workData
        }

        fun build(pairs: List<Pair<String, Any?>>): WorkData {
            val workData = WorkData()
            workData.putAll(pairs)
            return workData
        }
    }
}