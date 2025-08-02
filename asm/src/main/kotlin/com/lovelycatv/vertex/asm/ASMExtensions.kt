package com.lovelycatv.vertex.asm

import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor

/**
 * @author lovelycat
 * @since 2025-08-01 18:06
 * @version 1.0
 */
class ASMExtensions private constructor()

fun ClassWriter.visitMethod(
    modifiers: Array<JavaModifier>,
    name: String,
    parameters: Array<out TypeDeclaration> = arrayOf(),
    returnType: TypeDeclaration = TypeDeclaration.VOID
): MethodVisitor {
    return this.visitMethod(
        modifiers.sumOf { ASMUtils.toAccessCode(it) },
        name,
        parameters.toMethodDescriptor(returnType),
        null,
        null
    )
}

fun ClassWriter.visitField(
    modifiers: Array<JavaModifier>,
    name: String,
    type: TypeDeclaration,
    defaultValue: Any? = null
): FieldVisitor {
    return this.visitField(
        modifiers.sumOf { ASMUtils.toAccessCode(it) },
        name,
        type.getDescriptor(),
        null,
        defaultValue
    )
}

fun <T: TypeDeclaration> Array<T>.toMethodDescriptor(returnType: TypeDeclaration): String {
    val descriptor = this.joinToString(separator = "", prefix = "", postfix = "") { it.getDescriptor() }
    return "(${descriptor})${returnType.getDescriptor()}"
}