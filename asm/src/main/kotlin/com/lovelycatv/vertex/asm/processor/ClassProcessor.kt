package com.lovelycatv.vertex.asm.processor

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.lang.ClassDeclaration
import com.lovelycatv.vertex.asm.lang.MethodDeclaration
import com.lovelycatv.vertex.asm.visitField
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

/**
 * @author lovelycat
 * @since 2025-08-02 17:21
 * @version 1.0
 */
class ClassProcessor(
    val classWriter: ClassWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)
) {
    fun writeClass(classDeclaration: ClassDeclaration) {
        val methodProcessor = MethodProcessor(classDeclaration, this.classWriter)

        classWriter.visit(
            Opcodes.V1_8,
            classDeclaration.modifiers.sumOf { ASMUtils.toAccessCode(it) },
            classDeclaration.className,
            null,
            classDeclaration.superClass?.getInternalClassName() ?: ASMUtils.OBJECT_INTERNAL_NAME,
            classDeclaration.interfaces?.map { it.getDescriptor() }?.toTypedArray()
        )

        println("${classDeclaration.modifiers.map { it.name.lowercase() }.joinToString(separator = " ")} " +
            "${classDeclaration.className} " +
            "extends ${classDeclaration.superClass?.getInternalClassName()} " +
            "implements ${classDeclaration.interfaces?.map { it.getInternalClassName() }?.joinToString()}")

        classDeclaration.fields.forEach {
            classWriter.visitField(
                modifiers = it.modifiers,
                name = it.name,
                type = it.type,
                defaultValue = it.defaultValue
            ).visitEnd()

            println("${classDeclaration.modifiers.map { it.name.lowercase() }.joinToString(separator = " ")} " +
                "${it.name} ${it.type.getDescriptor()} = ${it.defaultValue}")
        }

        if (classDeclaration.constructors.isEmpty()) {
            methodProcessor.writeMethod(MethodDeclaration.noArgsConstructor())
        }

        classDeclaration.methods.forEach {
            methodProcessor.writeMethod(it)
        }

        classWriter.visitEnd()
    }
}