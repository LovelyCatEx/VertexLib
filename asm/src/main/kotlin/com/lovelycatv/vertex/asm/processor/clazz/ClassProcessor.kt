package com.lovelycatv.vertex.asm.processor.clazz

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.asm.lang.ClassDeclaration
import com.lovelycatv.vertex.asm.lang.MethodDeclaration
import com.lovelycatv.vertex.asm.processor.AbstractDeclarationProcessor
import com.lovelycatv.vertex.asm.processor.method.MethodProcessor
import com.lovelycatv.vertex.log.logger
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

/**
 * @author lovelycat
 * @since 2025-08-02 17:21
 * @version 1.0
 */
class ClassProcessor(
    val classWriter: ClassWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)
) : AbstractDeclarationProcessor() {
    private val log = logger()

    override val processorContext = Context(this)

    fun writeClass(classDeclaration: ClassDeclaration) {
        classWriter.visit(
            Opcodes.V1_8,
            classDeclaration.modifiers.sumOf { ASMUtils.toAccessCode(it) },
            classDeclaration.className,
            null,
            classDeclaration.superClass.getInternalClassName(),
            classDeclaration.interfaces.map { it.getDescriptor() }.toTypedArray()
        )

        VertexASMLog.log(log, "${classDeclaration.modifiers.joinToString(separator = " ") { it.name.lowercase() }} " +
            "${classDeclaration.className} " +
            "extends ${classDeclaration.superClass.getInternalClassName()} " +
            "implements ${classDeclaration.interfaces.joinToString { it.getInternalClassName() }}")

        this.processorContext.writeClass(classDeclaration, classWriter)
    }

    class Context(owner: ClassProcessor) : AbstractDeclarationProcessor.Context<ClassProcessor>(owner) {
        val fieldCodeProcessor = FieldCodeProcessor(this)

        lateinit var currentClassWriter: ClassWriter

        private fun init(classWriter: ClassWriter) {
            this.currentClassWriter = classWriter

            super.initialized()
        }

        /**
         * Write a method.
         *
         * @param clazz Target method
         * @param classWriter ClassWriter
         * @param beforeCode Before write field code.
         * @param afterCode After method code written and before write end.
         */
        fun writeClass(
            clazz: ClassDeclaration,
            classWriter: ClassWriter,
            beforeCode: (ClassWriter.() -> Unit)? = null,
            afterCode: (ClassWriter.() -> Unit)? = null
        ) {
            this.init(classWriter)

            beforeCode?.invoke(classWriter)

            /**
             * Process fields
             */
            clazz.fields.forEach {
                this.fieldCodeProcessor.processField(it)
            }

            /**
             * Process functions
             */
            val methodProcessor = MethodProcessor(clazz, classWriter)
            if (clazz.constructors.isEmpty()) {
                methodProcessor.writeMethod(MethodDeclaration.noArgsConstructor(parentClass = clazz))
            }

            clazz.methods.forEach {
                methodProcessor.writeMethod(it)
            }

            afterCode?.invoke(classWriter)

            classWriter.visitEnd()

            this.consume()
        }
    }
}