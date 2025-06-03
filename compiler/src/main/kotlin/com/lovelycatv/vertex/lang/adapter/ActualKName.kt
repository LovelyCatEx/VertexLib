package com.lovelycatv.vertex.lang.adapter

import com.lovelycatv.vertex.lang.model.KName

/**
 * @author lovelycat
 * @since 2025-05-30 15:31
 * @version 1.0
 */
class ActualKName(
    override val simpleName: String,
    override val qualifiedName: String?
) : KName {
    override fun toString() = this.qualifiedName ?: this.simpleName
}