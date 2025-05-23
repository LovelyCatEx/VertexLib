package com.lovelycatv.vertex.work.exception

import com.lovelycatv.vertex.work.scope.WorkCoroutineScope

/**
 * @author lovelycat
 * @since 2024-10-27 23:23
 * @version 1.0
 */
class WorkCoroutineScopeAwaitTimeoutException(
    workCoroutineScope: WorkCoroutineScope,
    timeout: Long
) : RuntimeException("""
        WorkCoroutineScope running time exceeds the set threshold timeout: $timeout, 
        ${workCoroutineScope.getActiveJobs().size} works still running, 
        ${workCoroutineScope.getInactiveJobs().size} works done
    """.trimIndent().replace("\n", ""))