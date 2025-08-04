package com.lovelycatv.vertex.asm

import com.lovelycatv.vertex.log.logger
import org.slf4j.Logger

/**
 * @author lovelycat
 * @since 2025-08-04 13:22
 * @version 1.0
 */
object VertexASMLog {
    private val DEBUGGING = ThreadLocal<Boolean>().apply { set(false) }
    private val IGNORE_WARNINGS = ThreadLocal<Boolean>().apply { set(false) }
    private val IGNORE_ERRORS = ThreadLocal<Boolean>().apply { set(false) }

    fun setEnableDebugging(enable: Boolean) {
        this.DEBUGGING.set(enable)
    }

    fun setIgnoreWarnings(ignore: Boolean) {
        this.IGNORE_WARNINGS.set(ignore)
    }

    fun setIgnoreErrors(ignore: Boolean) {
        this.IGNORE_ERRORS.set(ignore)
    }

    fun log(logger: Logger, text: String) {
        if (DEBUGGING.get()) {
            logger.info(text)
        }
    }

    fun log(logger: Logger, text: String, t: Throwable) {
        if (DEBUGGING.get()) {
            logger.info(text)
            t.printStackTrace()
        }
    }

    fun warn(logger: Logger, text: String) {
        if (!IGNORE_WARNINGS.get()) {
            println(text)
        }
    }

    fun warn(logger: Logger, text: String, t: Throwable) {
        if (!IGNORE_WARNINGS.get()) {
            logger.warn(text)
            t.printStackTrace()
        }
    }

    fun error(logger: Logger, text: String) {
        if (!IGNORE_ERRORS.get()) {
            logger.warn(text)
        }
    }

    fun error(logger: Logger, text: String, t: Throwable) {
        if (!IGNORE_ERRORS.get()) {
            logger.error(text)
            t.printStackTrace()
        }
    }
}