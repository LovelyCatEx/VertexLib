package com.lovelycatv.vertex.aspect.proxy

/**
 * @author lovelycat
 * @since 2025-08-11 02:23
 * @version 1.0
 */
fun interface ProxyNaming {
    fun getClassName(target: Class<*>): String
}