package com.lovelycatv.vertex.lang.model

import com.lovelycatv.vertex.annotation.processing.compareType
import javax.lang.model.element.*
import javax.lang.model.type.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * @author lovelycat
 * @since 2025-05-27 18:44
 * @version 1.0
 */
class TypeMirrorUtils private constructor()

@OptIn(ExperimentalContracts::class)
fun TypeMirror.isClassType(): Boolean {
    contract {
        returns(true) implies (this@isClassType is DeclaredType)
    }

    return this is DeclaredType
}

@OptIn(ExperimentalContracts::class)
fun TypeMirror.isPrimitiveType(): Boolean {
    contract {
        returns(true) implies (this@isPrimitiveType is PrimitiveType)
    }

    return this is PrimitiveType
}

@OptIn(ExperimentalContracts::class)
fun TypeMirror.isArrayType(): Boolean {
    contract {
        returns(true) implies (this@isArrayType is ArrayType)
    }

    return this is ArrayType
}

@OptIn(ExperimentalContracts::class)
fun TypeMirror.isNullType(): Boolean {
    contract {
        returns(true) implies (this@isNullType is NullType)
    }

    return this is NullType
}

@OptIn(ExperimentalContracts::class)
fun TypeMirror.isNoType(): Boolean {
    contract {
        returns(true) implies (this@isNoType is NoType)
    }

    return this is NoType
}

@OptIn(ExperimentalContracts::class)
fun TypeMirror.isVoidType(): Boolean {
    contract {
        returns(true) implies (this@isVoidType is NoType)
    }

    return this.isNoType() && this@isVoidType.kind == TypeKind.VOID
}

@OptIn(ExperimentalContracts::class)
fun TypeMirror.isPackageType(): Boolean {
    contract {
        returns(true) implies (this@isPackageType is NoType)
    }

    return this.isNoType() && this@isPackageType.kind == TypeKind.PACKAGE
}

@OptIn(ExperimentalContracts::class)
fun TypeMirror.isTypeVariable(): Boolean {
    contract {
        returns(true) implies (this@isTypeVariable is TypeVariable)
    }

    return this is TypeVariable
}

@OptIn(ExperimentalContracts::class)
fun TypeMirror.isWildcardType(): Boolean {
    contract {
        returns(true) implies (this@isWildcardType is WildcardType)
    }

    return this is WildcardType
}