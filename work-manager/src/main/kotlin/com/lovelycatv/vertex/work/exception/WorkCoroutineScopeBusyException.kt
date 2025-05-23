package com.lovelycatv.vertex.work.exception

/**
 * @author lovelycat
 * @since 2024-10-27 22:38
 * @version 1.0
 */
class WorkCoroutineScopeBusyException : RuntimeException("You should call WorkCoroutineScope\$initialize() before using it")