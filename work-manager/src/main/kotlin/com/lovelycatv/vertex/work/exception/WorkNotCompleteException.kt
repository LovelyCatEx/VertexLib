package com.lovelycatv.vertex.work.exception

import com.lovelycatv.vertex.work.base.BaseWork

/**
 * @author lovelycat
 * @since 2024-10-27 20:41
 * @version 1.0
 */
class WorkNotCompleteException(work: BaseWork) : RuntimeException("Work ${work.workName} is still running")