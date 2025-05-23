package com.lovelycatv.vertex.log

import org.slf4j.LoggerFactory

/**
 * @author lovelycat
 * @since 2025-02-15 19:00
 * @version 1.0
 */
class LogExtensions private constructor()

fun <T: Any> T.logger() = LoggerFactory.getLogger(this.javaClass)

fun <T: Any> T.logDebug(message: String) = this.logger().debug(message)

fun <T: Any> T.logDebug(message: String, t: Throwable) = this.logger().debug(message, t)

fun <T: Any> T.logDebug(message: String, vararg args: Any?) = this.logger().debug(message, *args)

fun <T: Any> T.logInfo(message: String) = this.logger().info(message)
fun <T: Any> T.logInfo(message: String, t: Throwable) = this.logger().info(message, t)

fun <T: Any> T.logInfo(message: String, vararg args: Any?) = this.logger().info(message, *args)

fun <T: Any> T.logWarn(message: String) = this.logger().warn(message)

fun <T: Any> T.logWarn(message: String, t: Throwable) = this.logger().warn(message, t)

fun <T: Any> T.logWarn(message: String, vararg args: Any?) = this.logger().warn(message, *args)

fun <T: Any> T.logError(message: String) = this.logger().error(message)

fun <T: Any> T.logError(message: String, t: Throwable) = this.logger().error(message, t)

fun <T: Any> T.logError(message: String, vararg args: Any?) = this.logger().error(message, *args)
