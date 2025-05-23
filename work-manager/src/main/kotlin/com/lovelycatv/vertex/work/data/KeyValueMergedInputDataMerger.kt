package com.lovelycatv.vertex.work.data

/**
 * @author lovelycat
 * @since 2024-10-31 21:41
 * @version 1.0
 */
class KeyValueMergedInputDataMerger : InputWorkDataMerger {
    override fun merge(results: List<WorkData>): WorkData {
        val pairs = results.flatMap { it.toPairList() }
        val keySet = pairs.map { it.first }.toSet()

        val savedPairs = mutableMapOf(*keySet.map { it to mutableListOf<Any?>() }.toTypedArray())

        keySet.forEach { key ->
            results.forEach { result ->
                result.toPairList().find { it.first == key }?.let {
                    savedPairs[key]!!.add(it.second)
                }
            }
        }

        return WorkData.build(savedPairs.toList())
    }
}