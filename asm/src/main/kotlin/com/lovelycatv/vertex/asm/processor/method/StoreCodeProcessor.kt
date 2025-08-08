package com.lovelycatv.vertex.asm.processor.method

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.JVMInstruction
import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.asm.exception.IllegalValueAccessException
import com.lovelycatv.vertex.asm.lang.code.store.*
import com.lovelycatv.vertex.log.logger
import com.lovelycatv.vertex.reflect.TypeUtils
import kotlin.math.floor

/**
 * @author lovelycat
 * @since 2025-08-06 16:50
 * @version 1.0
 */
class StoreCodeProcessor(
    private val context: MethodProcessor.Context
) {
    private val log = logger()

    fun processStoreValue(it: IStoreValue) {
        when (it) {
            is StoreLocalVariable -> {
                val targetVariable = context.currentVariables.getByName(it.variableName)
                    ?: throw IllegalValueAccessException("Variable ${it.variableName} not found.")
                val slot = targetVariable.slotIndex
                val instruction = ASMUtils.getStoreInstruction(targetVariable.type)

                context.currentMethodWriter.visitVarInsn(instruction.code, slot)
                VertexASMLog.log(log, "${instruction.name} $slot")

                context.currentStack.pop()
            }

            is StoreFieldVariable -> {
                val owner = it.targetClass?.let { TypeUtils.getInternalName(it) } ?: context.owner.parentClass.className.replace(".", "/")
                val fieldName = it.fieldName
                val fieldDescriptor = it.fieldType.getDescriptor()

                val instruction = if (it.isStatic) JVMInstruction.PUTSTATIC else JVMInstruction.PUTFIELD
                context.currentMethodWriter.visitFieldInsn(instruction.code, owner, fieldName, fieldDescriptor)

                VertexASMLog.log(log, "${instruction.name} $owner $fieldName $fieldDescriptor")

                context.currentStack.pop()
            }

            is PopValue -> {
                val p2Count = floor(1.0 * it.count / 2).toInt()
                val y = it.count % 2

                for (i in 0..<p2Count) {
                    context.currentMethodWriter.visitInsn(JVMInstruction.POP2.code)
                    VertexASMLog.log(log, "POP2")
                }

                for (i in 0..<y) {
                    context.currentMethodWriter.visitInsn(JVMInstruction.POP.code)
                    VertexASMLog.log(log, "POP")
                }

                for (i in 0..<it.count) {
                    context.currentStack.pop()
                }
            }

            is StoreArrayValue -> {
                val instruction = ASMUtils.getStoreInstructionForArrayValue(it.elementType.originalClass)

                // Load index
                it.index.forEach {
                    context.processMethodCode(it)
                }

                // Load new value
                it.newValue.forEach {
                    context.processMethodCode(it)
                }

                context.currentMethodWriter.visitInsn(instruction.code)
                VertexASMLog.log(log, instruction.name)

                context.currentStack.pop()
            }
        }
    }
}