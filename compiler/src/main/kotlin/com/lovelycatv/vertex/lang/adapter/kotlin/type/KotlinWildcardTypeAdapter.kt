package com.lovelycatv.vertex.lang.adapter.kotlin.type

import com.google.devtools.ksp.symbol.KSType
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinTypeAdapter
import com.lovelycatv.vertex.lang.model.annotation.KAnnotationMirror
import com.lovelycatv.vertex.lang.model.isPrimitiveType
import com.lovelycatv.vertex.lang.model.type.KPrimitiveType
import com.lovelycatv.vertex.lang.model.type.KTypeMirror
import com.lovelycatv.vertex.lang.model.type.KWildcardType
import com.lovelycatv.vertex.lang.util.AbstractKotlinAdapterContext
import com.lovelycatv.vertex.lang.util.toKAnnotations

/**
 * @author lovelycat
 * @since 2025-06-24 03:11
 * @version 1.0
 */
class KotlinWildcardTypeAdapter(
    context: AbstractKotlinAdapterContext
) : AbstractKotlinTypeAdapter<KSType, KWildcardType>(context) {
    override fun translate(type: KSType): KWildcardType {
        throw UnsupportedOperationException("There are no actual WildcardType representation in ksp, possibly be found in typeArguments of KDeclaredType")
    }
}