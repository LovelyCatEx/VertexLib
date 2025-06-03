package com.lovelycatv.vertex.lang.model

import com.lovelycatv.vertex.lang.modifier.IModifier

/**
 * @author lovelycat
 * @since 2025-05-29 23:43
 * @version 1.0
 */
interface KModifierContainer {
    val modifiers: Sequence<IModifier>
}