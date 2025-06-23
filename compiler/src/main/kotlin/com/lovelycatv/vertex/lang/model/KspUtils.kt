package com.lovelycatv.vertex.lang.model

import com.google.devtools.ksp.symbol.*
import kotlin.jvm.Throws

/**
 * @author lovelycat
 * @since 2025-06-23 19:19
 * @version 1.0
 */
class KspUtils private constructor()

private const val KotlinArray = "kotlin.Array"
private const val KotlinString = "kotlin.String"
private const val KotlinShort = "kotlin.Short"
private const val KotlinInt = "kotlin.Int"
private const val KotlinLong = "kotlin.Long"
private const val KotlinBoolean = "kotlin.Boolean"
private const val KotlinAny = "kotlin.Any"

fun KSAnnotated.getPackageName(): String {
    return when (this) {
        is KSFile -> this.packageName.asString()

        is KSDeclaration -> this.packageName.asString()

        is KSTypeReference -> {
            this.resolve().declaration.packageName.asString()
        }

        is KSTypeArgument -> {
            this.type?.resolve()?.declaration?.packageName?.asString()
                ?: throw IllegalStateException("KSTypeArgument's resolved type has no package name.")
        }

        else -> throw IllegalStateException("Unsupported KSAnnotated type: ${this::class.simpleName}")
    }
}

fun KSClassDeclaration.getSuperClass(): KSTypeReference {
    return this.superTypes.firstOrNull {
        (it.resolve().declaration as? KSClassDeclaration)?.classKind == ClassKind.CLASS
    } ?: throw IllegalStateException("All classes should be Kotlin.Any's subclass, current superTypes: ${this.superTypes.map { "${it.resolve()}(${(it.resolve() as? KSClassDeclaration)?.classKind})" }.toList()}")
}

fun KSClassDeclaration.getInterfaces(): Sequence<KSTypeReference> {
    return this.superTypes.filter {
        val r = it.resolve() as? KSClassDeclaration
        r != null && r.classKind == ClassKind.INTERFACE
    }
}

fun Sequence<KSAnnotation>.getJVMThrowsAnnotation(): Sequence<KSAnnotation> {
    return this.filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == Throws::class.qualifiedName }
}

fun KSAnnotated.getJVMThrowsAnnotation(): Sequence<KSAnnotation> {
    return this.annotations.getJVMThrowsAnnotation()
}

fun KSType.isClassType(): Boolean {
    return this.declaration is KSClassDeclaration
}

fun KSType.isFunctionType(): Boolean {
    return this.declaration is KSFunctionDeclaration
}

fun KSType.isPropertyType(): Boolean {
    return this.declaration is KSPropertyDeclaration
}

fun KSType.isTypeParameter(): Boolean {
    return this.declaration is KSTypeParameter
}

fun KSType.isTypeAlias(): Boolean {
    return this.declaration is KSTypeAlias
}

fun KSType.isArrayType(): Boolean {
    return this.declaration.qualifiedName?.asString() == KotlinArray
}

fun KSType.isPrimitiveType(): Boolean {
    return this.declaration.qualifiedName?.asString() in listOf(KotlinString, KotlinShort, KotlinInt, KotlinLong, KotlinBoolean)
}

fun KSType.isAnyType(): Boolean {
    return this.declaration.qualifiedName?.asString() == KotlinAny
}


fun KSClassDeclaration.findTopLevelAnyType(): KSType {
    var layer = this.superTypes

    while (!layer.any { it.resolve().isAnyType() }) {
        layer = layer.flatMap { (it.resolve().declaration as KSClassDeclaration).superTypes }
    }

    return layer.find { it.resolve().isAnyType() }!!.resolve()
}