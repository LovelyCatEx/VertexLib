package com.lovelycatv.vertex.asm.processor.method

import com.lovelycatv.vertex.asm.JavaModifier
import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.asm.lang.ClassDeclaration
import com.lovelycatv.vertex.asm.lang.MethodDeclaration
import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.IJavaCode
import com.lovelycatv.vertex.asm.lang.code.NopInstruction
import com.lovelycatv.vertex.asm.lang.code.calculate.ICalculation
import com.lovelycatv.vertex.asm.lang.code.define.IDefinition
import com.lovelycatv.vertex.asm.lang.code.load.ILoadValue
import com.lovelycatv.vertex.asm.lang.code.store.IStoreValue
import com.lovelycatv.vertex.asm.misc.MethodLocalStack
import com.lovelycatv.vertex.asm.misc.MethodLocalVariableMap
import com.lovelycatv.vertex.asm.processor.AbstractDeclarationProcessor
import com.lovelycatv.vertex.asm.visitMethod
import com.lovelycatv.vertex.log.logger
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author lovelycat
 * @since 2025-08-02 17:20
 * @version 1.0
 */
class MethodProcessor(
    val parentClass: ClassDeclaration,
    private val classWriter: ClassWriter
) : AbstractDeclarationProcessor() {
    private val log = logger()

    override val processorContext = Context(this)

    fun writeMethod(method: MethodDeclaration) {
        val methodWriter = classWriter.visitMethod(
            modifiers = method.modifiers,
            name = method.methodName,
            parameters = (method.parameters ?: emptyArray()),
            returnType = method.returnType ?: TypeDeclaration.VOID
        )

        if (!method.modifiers.contains(JavaModifier.STATIC)) {
            methodWriter.visitVarInsn(Opcodes.ALOAD, 0)
        }

        VertexASMLog.log(log, "${method.modifiers.joinToString { it.name.lowercase() }} ${method.methodName} ${method.getDescriptor()}")

        this.processorContext.writeMethod(method, methodWriter)
    }


    class Context(owner: MethodProcessor) : AbstractDeclarationProcessor.Context<MethodProcessor>(owner) {
        val definitionCodeProcessor: DefinitionCodeProcessor = DefinitionCodeProcessor(this)
        val loadCodeProcessor: LoadCodeProcessor = LoadCodeProcessor(this)
        val storeCodeProcessor: StoreCodeProcessor = StoreCodeProcessor(this)
        val calculationCodeProcessor: CalculationCodeProcessor = CalculationCodeProcessor(this)

        lateinit var currentMethodWriter: MethodVisitor
            private set
        lateinit var currentMethod: MethodDeclaration
            private set
        lateinit var currentStack: MethodLocalStack
            private set
        lateinit var currentVariables: MethodLocalVariableMap
            private set

        /**
         * This function must be called before writing a new method.
         *
         * @param method Target method
         * @param methodWriter MethodVisitor
         * @param stack MethodLocalStack
         * @param variables MethodLocalVariableMap
         */
        private fun init(method: MethodDeclaration, methodWriter: MethodVisitor, stack: MethodLocalStack, variables: MethodLocalVariableMap) {
            this.currentMethod = method
            this.currentMethodWriter = methodWriter
            this.currentStack = stack
            this.currentVariables = variables

            super.initialized()
        }

        /**
         * Write a method.
         *
         * @param method Target method
         * @param methodWriter MethodVisitor
         * @param stack MethodLocalStack
         * @param variables MethodLocalVariableMap
         * @param beforeCode Before write method code.
         * @param afterCode After method code written and before write max stack/locals.
         */
        fun writeMethod(
            method: MethodDeclaration,
            methodWriter: MethodVisitor,
            stack: MethodLocalStack = MethodLocalStack(),
            variables: MethodLocalVariableMap = MethodLocalVariableMap(method),
            beforeCode: (MethodVisitor.() -> Unit)? = null,
            afterCode: (MethodVisitor.() -> Unit)? = null
        ) {
            this.init(method, methodWriter, stack, variables)

            beforeCode?.invoke(methodWriter)

            method.getCodeList().forEach {
                this.processMethodCode(it)
            }

            afterCode?.invoke(methodWriter)

            methodWriter.visitMaxs(-1, -1)
            methodWriter.visitEnd()

            super.consume()
        }

        fun processMethodCode(code: IJavaCode) {
            when (code) {
                is IDefinition -> this.definitionCodeProcessor.processDefinition(code)
                is ILoadValue -> this.loadCodeProcessor.processLoadValue(code)
                is IStoreValue -> this.storeCodeProcessor.processStoreValue(code)
                is ICalculation -> this.calculationCodeProcessor.processCalculation(code)
                else -> when (code) {
                    is NopInstruction -> this.currentMethodWriter.visitInsn(Opcodes.NOP)
                }
            }
        }
    }
}