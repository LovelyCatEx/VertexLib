package com.lovelycatv.vertex.lang.adapter

/**
 * @author lovelycat
 * @since 2025-06-01 19:06
 * @version 1.0
 */
abstract class AbstractAdapter<A: Any, E: Any, T: Any>(
    protected val context: IAdapterContext<A, E, T>
)