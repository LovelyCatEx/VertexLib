package com.lovelycatv.vertex.asm.processor.method

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.JVMInstruction
import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.asm.exception.IllegalFunctionAccessException
import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.FunctionInvocationType
import com.lovelycatv.vertex.asm.lang.code.define.*
import com.lovelycatv.vertex.asm.lang.code.load.LoadConstantValue
import com.lovelycatv.vertex.asm.lang.code.load.LoadFieldValue
import com.lovelycatv.vertex.asm.lang.code.load.LoadNull
import com.lovelycatv.vertex.log.logger
import org.objectweb.asm.Opcodes

/**
 * @author lovelycat
 * @since 2025-08-06 16:48
 * @version 1.0
 */
class DefinitionCodeProcessor(private val context: MethodProcessor.Context) {
    private val log = logger()

    fun processDefinition(it: IDefinition) {
        when (it) {
            is DefineLocalVariable -> {
                val instruction = ASMUtils.getStoreInstruction(it.type)
                val record = context.currentVariables.add(it.name, it.type)

                val preInstruction = when (it.type.originalClass) {
                    Boolean::class.java -> LoadConstantValue(false)
                    Byte::class.java -> LoadConstantValue(0.toByte())
                    Char::class.java -> LoadConstantValue(0.toChar())
                    Short::class.java -> LoadConstantValue(0.toShort())
                    Int::class.java -> LoadConstantValue(0)
                    Long::class.java -> LoadConstantValue(0L)
                    Float::class.java -> LoadConstantValue(0f)
                    Double::class.java -> LoadConstantValue(0.0)
                    else -> LoadNull()
                }

                context.loadCodeProcessor.processLoadValue(preInstruction)

                VertexASMLog.log(log, "${instruction.name} ${record.slotIndex}")
            }

            is DefineNewInstance -> {
                // GET_FIELD of this instance will use 1 more load instruction (ALOAD 0)
                if (it.constructorParameters.size != (it.args.size - it.args.count { it is LoadFieldValue })) {
                    throw IllegalFunctionAccessException("Trying new an instance of ${it.clazz.canonicalName}" +
                        " but count of args(${it.args.size}) is not equals to parameters(${it.constructorParameters.size}).")
                }

                val className = ASMUtils.getInternalName(it.clazz)
                context.currentMethodWriter.visitTypeInsn(JVMInstruction.NEW.code, className)

                // As the <init> function calling will consume an instance in stack,
                // Copy a duplicate instance to the top of the stack
                context.currentMethodWriter.visitInsn(JVMInstruction.DUP.code)

                // Prepare parameters
                it.args.forEach {
                    context.loadCodeProcessor.processLoadValue(it)
                }

                // Call constructor
                val initFunctionName = ASMUtils.CONSTRUCTOR_NAME
                val functionDescriptor = it.getConstructorDescriptor()
                context.currentMethodWriter.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    className,
                    initFunctionName,
                    functionDescriptor,
                    false
                )

                for (i in 0..<it.args.size) {
                    context.currentStack.pop()
                }

                context.currentStack.push(TypeDeclaration.fromClass(it.clazz))

                VertexASMLog.log(log, "INVOKESPECIAL $className $initFunctionName $functionDescriptor")
            }

            is DefineFunctionInvocation -> {
                // GET_FIELD of this instance will use 1 more load instruction (ALOAD 0)
                if (it.parameters.size != (it.args.size - it.args.count { it is LoadFieldValue })) {
                    throw IllegalFunctionAccessException("Trying call ${it.methodName}() of ${it.owner.canonicalName}" +
                        " but count of args(${it.args.size}) is not equals to parameters(${it.parameters.size}).")
                }

                val owner = ASMUtils.getInternalName(it.owner)
                val methodName = it.methodName
                val descriptor = it.getDescriptor()

                // Prepare parameters
                it.args.forEach {
                    context.loadCodeProcessor.processLoadValue(it)
                }

                // Invoke function
                val instruction = when (it.type) {
                    FunctionInvocationType.NORMAL -> JVMInstruction.INVOKEVIRTUAL

                    FunctionInvocationType.PRIVATE,
                    FunctionInvocationType.SUPER -> JVMInstruction.INVOKESPECIAL

                    FunctionInvocationType.INTERFACE -> JVMInstruction.INVOKEINTERFACE

                    FunctionInvocationType.STATIC -> JVMInstruction.INVOKESTATIC
                }

                context.currentMethodWriter.visitMethodInsn(
                    instruction.code,
                    owner,
                    methodName,
                    descriptor,
                    false
                )

                for (i in 0..<it.args.size) {
                    context.currentStack.pop()
                }

                if (it.hasReturnType()) {
                    context.currentStack.push(it.returnType)
                }

                VertexASMLog.log(log, "${instruction.name} $owner $methodName $descriptor")
            }

            is DefineReturn -> {
                val instruction = ASMUtils.getReturnInstruction(context.currentMethod.returnType?.originalClass ?: Void::class.java)
                context.currentMethodWriter.visitInsn(instruction.code)
                VertexASMLog.log(log, instruction.name)
            }

            is DefineTypeCast -> {
                if (it.from != null) {
                    val instruction = ASMUtils.getPrimitiveCastInstruction(it.from.originalClass, it.target.originalClass)
                    context.currentMethodWriter.visitInsn(instruction.code)
                    VertexASMLog.log(log, instruction.name)
                } else {
                    val instruction = JVMInstruction.CHECKCAST
                    val targetInternalName = it.target.getInternalClassName()
                    context.currentMethodWriter.visitTypeInsn(instruction.code, targetInternalName)
                    VertexASMLog.log(log, "${instruction.name} $targetInternalName")
                }
            }
        }
    }
}