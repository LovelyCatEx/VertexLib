package com.lovelycatv.vertex.lang.adapter.kotlin.element

import com.google.devtools.ksp.symbol.*
import com.lovelycatv.vertex.lang.adapter.AbstractAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotated
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.element.KElement
import com.lovelycatv.vertex.lang.model.element.KFile
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext
import com.lovelycatv.vertex.lang.util.getKAnnotations

/**
 * @author lovelycat
 * @since 2025-06-26 02:49
 * @version 1.0
 */
class KotlinFileElementAdapter(
    context: IKotlinAdapterContext
) : AbstractAdapter<KSAnnotation, KSAnnotated, KSType>(context) {
    fun translate(file: KSFile): KFile {
        return object : KFile {
            override val original: Any
                get() = file
            override val language: KAnnotated.Language
                get() = KAnnotated.Language.KOTLIN
            override val packageName: String
                get() = file.packageName.asString()
            override val fileName: String
                get() = file.fileName
            override val filePath: String
                get() = file.filePath
            override val declarations: Sequence<KElement<*>>
                get() = file.declarations.map { context.translateElement(it) }
            override val annotations: Sequence<KAnnotationMirror>
                get() = file.getKAnnotations(context)

        }
    }
}