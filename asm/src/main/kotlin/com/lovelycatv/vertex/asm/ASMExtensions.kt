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
    parameters: Array<Class<*>> = arrayOf(),
    returnType: Class<*> = Void::class.java
): MethodVisitor {
    return this.visitMethod(
        modifiers.sumOf { ASMUtils.toAccessCode(it) },
        name,
        "${parameters.joinToString(separator = "", prefix = "(", postfix = ")") { ASMUtils.getDescriptor(it) }}${ASMUtils.getDescriptor(returnType)}",
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