package com.lovelycatv.vertex.lang.adapter.kotlin.type

import com.google.devtools.ksp.symbol.KSType
import com.lovelycatv.vertex.lang.adapter.kotlin.AbstractKotlinTypeAdapter
import com.lovelycatv.vertex.lang.model.type.KWildcardType
import com.lovelycatv.vertex.lang.util.IKotlinAdapterContext

/**
 * @author lovelycat
 * @since 2025-06-24 03:11
 * @version 1.0
 */
class KotlinWildcardTypeAdapter(
    context: IKotlinAdapterContext
) : AbstractKotlinTypeAdapter<KSType, KWildcardType>(context) {
    override fun translate(type: KSType): KWildcardType {
        throw UnsupportedOperationException("There are no actual WildcardType representation in ksp, possibly be found in typeArguments of KDeclaredType")
    }
}