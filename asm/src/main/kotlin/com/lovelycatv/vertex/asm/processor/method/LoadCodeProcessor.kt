package com.lovelycatv.vertex.asm.processor.method

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.JVMInstruction
import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.asm.exception.IllegalValueAccessException
import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.load.*
import com.lovelycatv.vertex.log.logger

/**
 * @author lovelycat
 * @since 2025-08-06 16:50
 * @version 1.0
 */
class LoadCodeProcessor(private val context: MethodProcessor.Context) {
    private val log = logger()

    fun processLoadValue(it: ILoadValue) {
        when (it) {
            is LoadThis -> {
                context.currentMethodWriter.visitVarInsn(JVMInstruction.ALOAD.code, 0)
                VertexASMLog.log(log, "${JVMInstruction.ALOAD.name} 0")

                context.currentStack.push(TypeDeclaration.OBJECT)
            }

            is LoadMethodParameter -> {
                val parameterIndex = it.index
                val actualIndex = parameterIndex + 1
                val targetParameter = context.currentMethod.actualParameters[parameterIndex]
                val instruction = ASMUtils.getLoadInstruction(targetParameter)

                context.currentMethodWriter.visitVarInsn(instruction.code, actualIndex)
                VertexASMLog.log(log, "${instruction.name} $actualIndex")

                context.currentStack.push(targetParameter)
            }

            is LoadLocalVariable -> {
                val targetVariable = context.currentVariables.getByName(it.variableName)
                    ?: throw IllegalValueAccessException("Variable ${it.variableName} not found.")
                val slot = targetVariable.slotIndex
                val instruction = ASMUtils.getLoadInstruction(targetVariable.type)

                context.currentMethodWriter.visitVarInsn(instruction.code, slot)
                VertexASMLog.log(log, "${instruction.name} $slot")

                context.currentStack.push(targetVariable.type)
            }

            is LoadConstantValue -> {
                if (it.value != null) {
                    val constInstruction = ASMUtils.getLoadConstInstruction(it.value)

                    if (constInstruction == JVMInstruction.LDC) {
                        context.currentStack.push(TypeDeclaration.fromClass(it.value::class.java))
                        context.currentMethodWriter.visitLdcInsn(it.value)

                        val instructionName = if (it.value is Long || it.value is Double) {
                            "LDC2_W"
                        } else constInstruction.name

                        VertexASMLog.log(log, "$instructionName ${it.value}")
                    } else if (constInstruction.instructionOnly) {
                        context.currentMethodWriter.visitInsn(constInstruction.code)
                        VertexASMLog.log(log, constInstruction.name)
                    } else {
                        context.currentMethodWriter.visitIntInsn(constInstruction.code, it.value.toString().toInt())
                        VertexASMLog.log(log, "${constInstruction.name} ${it.value}")
                    }

                    context.currentStack.push(TypeDeclaration.fromClass(it.value::class.java))
                } else{
                    this.processLoadValue(LoadNull())
                }
            }

            is LoadNull -> {
                context.currentMethodWriter.visitInsn(JVMInstruction.ACONST_NULL.code)
                VertexASMLog.log(log, "ACONST_NULL")

                // context.currentStack.push()
            }

            is LoadFieldValue -> {
                val owner = it.targetClass?.let { ASMUtils.getInternalName(it) } ?: context.owner.parentClass.className.replace(".", "/")
                val fieldName = it.fieldName
                val fieldDescriptor = ASMUtils.getDescriptor(it.fieldType)

                val instruction = if (it.isStatic) JVMInstruction.GETSTATIC else JVMInstruction.GETFIELD
                context.currentMethodWriter.visitFieldInsn(instruction.code, owner, fieldName, fieldDescriptor)
                VertexASMLog.log(log, "${instruction.name} $owner $fieldName $fieldDescriptor")

                context.currentStack.push(TypeDeclaration.fromClass(it.fieldType))
            }

            is LoadArray -> {
                when (val instruction = ASMUtils.getNewArrayInstruction(it.elementType.originalClass, it.dimensions)) {
                    JVMInstruction.MULTIANEWARRAY -> {
                        it.lengths.forEach {
                            this.processLoadValue(LoadConstantValue(it))
                        }
                        val descriptor = ASMUtils.getArrayDescriptor(it.elementType.originalClass, it.dimensions)
                        context.currentMethodWriter.visitMultiANewArrayInsn(descriptor, it.dimensions)
                        VertexASMLog.log(log, "${instruction.name} $descriptor ${it.dimensions}")

                        // context.currentStack.push()
                    }

                    JVMInstruction.NEWARRAY -> {
                        this.processLoadValue(LoadConstantValue(it.lengths[0]))
                        val operand = ASMUtils.getOperandForNewPrimitiveArray(it.elementType.originalClass)
                        context.currentMethodWriter.visitIntInsn(instruction.code, operand.code)
                        VertexASMLog.log(log, "${instruction.name} $operand")

                        // context.currentStack.push()
                    }

                    JVMInstruction.ANEWARRAY -> {
                        this.processLoadValue(LoadConstantValue(it.lengths[0]))
                        val internalName = it.elementType.getInternalClassName()
                        context.currentMethodWriter.visitTypeInsn(instruction.code, internalName)
                        VertexASMLog.log(log, "${instruction.name} $internalName")

                        // context.currentStack.push()
                    }

                    else -> {
                        throw IllegalArgumentException("Unsupported type of instruction for load array: ${instruction.name}.")
                    }
                }
            }

            is LoadArrayValue -> {
                val instruction = ASMUtils.getLoadInstructionForArrayValue(it.elementType.originalClass)

                // Load index
                it.index.forEach {
                    context.processMethodCode(it)
                }

                context.currentMethodWriter.visitInsn(instruction.code)
                VertexASMLog.log(log, instruction.name)

                context.currentStack.push(it.elementType)
            }
        }
    }
}