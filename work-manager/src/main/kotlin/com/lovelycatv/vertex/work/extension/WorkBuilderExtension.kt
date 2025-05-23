package com.lovelycatv.vertex.work.extension

import com.lovelycatv.vertex.work.base.AbstractWork
import com.lovelycatv.vertex.work.worker.WrappedWork
import java.util.UUID

/**
 * @author lovelycat
 * @since 2024-10-31 18:57
 * @version 1.0
 */
class WorkBuilderExtension private constructor()

inline fun <reified W: AbstractWork> WorkBuilder(): WrappedWork.Builder<W> {
    return WrappedWork.Builder(UUID.randomUUID().toString(), W::class)
}