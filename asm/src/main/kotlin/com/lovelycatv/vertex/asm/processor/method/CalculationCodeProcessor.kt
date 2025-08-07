package com.lovelycatv.vertex.asm.processor.method

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.JVMInstruction
import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.asm.exception.IllegalValueAccessException
import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.calculate.Calculation
import com.lovelycatv.vertex.asm.lang.code.calculate.ICalculation
import com.lovelycatv.vertex.asm.lang.code.calculate.VariableIncrement
import com.lovelycatv.vertex.log.logger

/**
 * @author lovelycat
 * @since 2025-08-07 12:19
 * @version 1.0
 */
class CalculationCodeProcessor(private val context: MethodProcessor.Context) {
    private val log = logger()

    fun processCalculation(it: ICalculation) {
        when (it) {
            is Calculation -> {
                val instruction = ASMUtils.getCalculationInstruction(it.type, it.numberType.originalClass)

                context.currentMethodWriter.visitInsn(instruction.code)

                context.currentStack.pop()
                context.currentStack.pop()
                context.currentStack.push(it.numberType)

                VertexASMLog.log(log, instruction.name)
            }

            is VariableIncrement -> {
                val targetVariable = context.currentVariables.getByName(it.variableName)
                    ?: throw IllegalValueAccessException("Variable ${it.variableName} not found.")
                val slot = targetVariable.slotIndex

                context.currentMethodWriter.visitIincInsn(slot, it.delta)

                VertexASMLog.log(log, "${JVMInstruction.IINC} $slot ${it.delta}")
            }
        }
    }
}