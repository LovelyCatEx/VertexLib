package com.lovelycatv.vertex.lang.model.element

import com.google.devtools.ksp.symbol.KSName
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationContainer

/**
 * @author lovelycat
 * @since 2025-05-29 23:09
 * @version 1.0
 */
interface KFile : KElementContainer, KAnnotationContainer {
    val packageName: KSName

    val fileName: String

    val filePath: String
}