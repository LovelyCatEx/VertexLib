package com.lovelycatv.vertex.lang.model.element

import com.google.devtools.ksp.symbol.KSName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotated

/**
 * @author lovelycat
 * @since 2025-05-29 23:09
 * @version 1.0
 */
interface KFile : KElementContainer, KAnnotated {
    val packageName: KSName

    val fileName: String

    val filePath: String
}