package com.lovelycatv.vertex.asm.processor

import com.lovelycatv.vertex.asm.*
import com.lovelycatv.vertex.asm.exception.IllegalFunctionAccessException
import com.lovelycatv.vertex.asm.exception.IllegalValueAccessException
import com.lovelycatv.vertex.asm.lang.ClassDeclaration
import com.lovelycatv.vertex.asm.lang.MethodDeclaration
import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.FunctionInvocationType
import com.lovelycatv.vertex.asm.lang.code.IJavaCode
import com.lovelycatv.vertex.asm.lang.code.define.*
import com.lovelycatv.vertex.asm.lang.code.load.*
import com.lovelycatv.vertex.asm.lang.code.store.*
import com.lovelycatv.vertex.asm.misc.MethodLocalStack
import com.lovelycatv.vertex.asm.misc.MethodLocalVariableMap
import com.lovelycatv.vertex.log.logger
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import kotlin.math.floor

/**
 * @author lovelycat
 * @since 2025-08-02 17:20
 * @version 1.0
 */
class MethodProcessor(
    private val parentClass: ClassDeclaration,
    private val classWriter: ClassWriter
) {
    private val log = logger()

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

        val localStack = MethodLocalStack()
        val localVariableMap = MethodLocalVariableMap(method)

        method.getCodeList().forEach {
            processMethodCode(methodWriter, method, localStack, localVariableMap, it)
        }

        methodWriter.visitMaxs(-1, -1)
        methodWriter.visitEnd()
    }

    private fun processMethodCode(
        methodWriter: MethodVisitor,
        method: MethodDeclaration,
        localStack: MethodLocalStack,
        localVariableMap: MethodLocalVariableMap,
        code: IJavaCode
    ) {
        when (code) {
            is ILoadValue -> this.processLoadValue(methodWriter, method, localStack, localVariableMap, code)
            is IStoreValue -> this.processStoreValue(methodWriter, method, localStack, localVariableMap, code)
            is IDefinition -> this.processDefinition(methodWriter, method, localStack, localVariableMap, code)
        }
    }

    private fun processStoreValue(
        methodWriter: MethodVisitor,
        method: MethodDeclaration,
        methodLocalStack: MethodLocalStack,
        methodLocalVariableMap: MethodLocalVariableMap,
        it: IStoreValue
    ) {
        when (it) {
            is StoreLocalVariable -> {
                val targetVariable = methodLocalVariableMap.getByName(it.variableName)
                    ?: throw IllegalValueAccessException("Variable ${it.variableName} not found")
                val slot = targetVariable.slotIndex
                val instruction = ASMUtils.getStoreOpcode(targetVariable.type)

                methodLocalStack.pop()

                methodWriter.visitVarInsn(instruction.code, slot)
                VertexASMLog.log(log, "${instruction.name} $slot")
            }

            is StoreFieldVariable -> {
                val owner = it.targetClass?.let { ASMUtils.getInternalName(it) } ?: parentClass.className.replace(".", "/")
                val fieldName = it.fieldName
                val fieldDescriptor = it.fieldType.getDescriptor()

                methodLocalStack.pop()

                val instruction = if (it.isStatic) JVMInstruction.PUTSTATIC else JVMInstruction.PUTFIELD
                methodWriter.visitFieldInsn(instruction.code, owner, fieldName, fieldDescriptor)

                VertexASMLog.log(log, "${instruction.name} $owner $fieldName $fieldDescriptor")
            }

            is PopValue -> {
                val p2Count = floor(1.0 * it.count / 2).toInt()
                val y = it.count % 2

                for (i in 0..<p2Count) {
                    methodWriter.visitInsn(JVMInstruction.POP2.code)
                    VertexASMLog.log(log, "POP2")
                }

                for (i in 0..<y) {
                    methodWriter.visitInsn(JVMInstruction.POP.code)
                    VertexASMLog.log(log, "POP")
                }

                for (i in 0..<it.count) {
                    methodLocalStack.pop()
                }
            }

            is StoreArrayValue -> {
                val instruction = ASMUtils.getStoreOpcodeForArrayValue(it.elementType.originalClass)

                // Load index
                it.index.forEach {
                    if (it is LoadConstantValue) {
                        methodWriter.visitIntInsn(Opcodes.BIPUSH, it.value as Int)
                        VertexASMLog.log(log, "LDC ${it.value}")
                    } else {
                        this.processMethodCode(methodWriter, method, methodLocalStack, methodLocalVariableMap, it)
                    }
                }

                // Load new value
                it.newValue.forEach {
                    this.processMethodCode(methodWriter, method, methodLocalStack, methodLocalVariableMap, it)
                }

                methodWriter.visitInsn(instruction.code)
                VertexASMLog.log(log, instruction.name)
            }
        }
    }

    private fun processLoadValue(
        methodWriter: MethodVisitor,
        method: MethodDeclaration,
        methodLocalStack: MethodLocalStack,
        methodLocalVariableMap: MethodLocalVariableMap,
        it: ILoadValue
    ) {
        when (it) {
            is LoadThis -> {
                methodWriter.visitVarInsn(JVMInstruction.ALOAD.code, 0)
                VertexASMLog.log(log, "${JVMInstruction.ALOAD.name} 0")
            }

            is LoadMethodParameter -> {
                val index = it.index + 1
                val targetParameter = method.actualParameters[index - 1]
                val instruction = ASMUtils.getLoadOpcode(targetParameter)

                methodLocalStack.push(targetParameter)

                methodWriter.visitVarInsn(instruction.code, index)
                VertexASMLog.log(log, "${instruction.name} $index")
            }

            is LoadLocalVariable -> {
                val targetVariable = methodLocalVariableMap.getByName(it.variableName)
                    ?: throw IllegalValueAccessException("Variable ${it.variableName} not found")
                val slot = targetVariable.slotIndex
                val instruction = ASMUtils.getLoadOpcode(targetVariable.type)

                methodLocalStack.push(targetVariable.type)

                methodWriter.visitVarInsn(instruction.code, slot)
                VertexASMLog.log(log, "${instruction.name} $slot")
            }

            is LoadConstantValue -> {
                if (it.value != null) {
                    methodLocalStack.push(TypeDeclaration.fromClass(it.value::class.java))
                    methodWriter.visitLdcInsn(it.value)

                    VertexASMLog.log(log, "LDC ${it.value}")
                } else{
                    methodWriter.visitInsn(JVMInstruction.ACONST_NULL.code)
                    VertexASMLog.log(log, "ACONST_NULL")
                }
            }

            is LoadNull -> {
                methodWriter.visitInsn(JVMInstruction.ACONST_NULL.code)
                VertexASMLog.log(log, "ACONST_NULL")
            }

            is LoadFieldValue -> {
                val owner = it.targetClass?.let { ASMUtils.getInternalName(it) } ?: parentClass.className.replace(".", "/")
                val fieldName = it.fieldName
                val fieldDescriptor = ASMUtils.getDescriptor(it.fieldType)

                methodLocalStack.push(TypeDeclaration.fromClass(it.fieldType))

                val instruction = if (it.isStatic) JVMInstruction.GETSTATIC else JVMInstruction.GETFIELD
                methodWriter.visitFieldInsn(instruction.code, owner, fieldName, fieldDescriptor)
                VertexASMLog.log(log, "${instruction.name} $owner $fieldName $fieldDescriptor")
            }

            is LoadArray -> {
                when (val instruction = ASMUtils.getNewArrayOpcode(it.elementType.originalClass, it.dimensions)) {
                    JVMInstruction.MULTIANEWARRAY -> {
                        it.lengths.forEach {
                            this.processLoadValue(methodWriter, method, methodLocalStack, methodLocalVariableMap, LoadConstantValue(it))
                        }
                        val descriptor = ASMUtils.getArrayDescriptor(it.elementType.originalClass, it.dimensions)
                        methodWriter.visitMultiANewArrayInsn(descriptor, it.dimensions)
                        VertexASMLog.log(log, "${instruction.name} $descriptor ${it.dimensions}")
                    }

                    JVMInstruction.NEWARRAY -> {
                        this.processLoadValue(methodWriter, method, methodLocalStack, methodLocalVariableMap, LoadConstantValue(it.lengths[0]))
                        val operand = ASMUtils.getOperandForNewPrimitiveArray(it.elementType.originalClass)
                        methodWriter.visitIntInsn(instruction.code, operand.code)
                        VertexASMLog.log(log, "${instruction.name} $operand")
                    }

                    JVMInstruction.ANEWARRAY -> {
                        this.processLoadValue(methodWriter, method, methodLocalStack, methodLocalVariableMap, LoadConstantValue(it.lengths[0]))
                        val internalName = it.elementType.getInternalClassName()
                        methodWriter.visitTypeInsn(instruction.code, internalName)
                        VertexASMLog.log(log, "${instruction.name} $internalName")
                    }

                    else -> {
                        throw IllegalArgumentException("Unsupported type of instruction for load array: ${instruction.name}")
                    }
                }
            }

            is LoadArrayValue -> {
                val instruction = ASMUtils.getLoadOpcodeForArrayValue(it.elementType.originalClass)

                // Load index
                it.index.forEach {
                    if (it is LoadConstantValue) {
                        methodWriter.visitIntInsn(Opcodes.LDC, it.value as Int)
                        VertexASMLog.log(log, "LDC ${it.value}")
                    } else {
                        this.processMethodCode(methodWriter, method, methodLocalStack, methodLocalVariableMap, it)
                    }
                }

                methodWriter.visitInsn(instruction.code)
                VertexASMLog.log(log, instruction.name)
            }
        }
    }

    private fun processDefinition(
        methodWriter: MethodVisitor,
        method: MethodDeclaration,
        methodLocalStack: MethodLocalStack,
        methodLocalVariableMap: MethodLocalVariableMap,
        it: IDefinition
    ) {
        when (it) {
            is DefineLocalVariable -> {
                val instruction = ASMUtils.getStoreOpcode(it.type)
                val record = methodLocalVariableMap.add(it.name, it.type)

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

                this.processLoadValue(methodWriter, method, methodLocalStack, methodLocalVariableMap, preInstruction)

                VertexASMLog.log(log, "${instruction.name} ${record.slotIndex}")
            }

            is DefineNewInstance -> {
                // GET_FIELD of this instance will use 1 more load instruction (ALOAD 0)
                if (it.constructorParameters.size != (it.args.size - it.args.count { it is LoadFieldValue })) {
                    throw IllegalFunctionAccessException("Trying new an instance of ${it.clazz.canonicalName}" +
                        " but count of args(${it.args.size}) is not equals to parameters(${it.constructorParameters.size})")
                }

                val className = ASMUtils.getInternalName(it.clazz)
                methodWriter.visitTypeInsn(JVMInstruction.NEW.code, className)

                // As the <init> function calling will consume an instance in stack,
                // Copy a duplicate instance to the top of the stack
                methodWriter.visitInsn(JVMInstruction.DUP.code)

                // Prepare parameters
                it.args.forEach {
                    processMethodCode(methodWriter, method, methodLocalStack, methodLocalVariableMap, it)
                }

                // Call constructor
                val initFunctionName = ASMUtils.CONSTRUCTOR_NAME
                val functionDescriptor = it.getConstructorDescriptor()
                methodWriter.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    className,
                    initFunctionName,
                    functionDescriptor,
                    false
                )

                for (i in 0..<it.args.size) {
                    methodLocalStack.pop()
                }

                methodLocalStack.push(TypeDeclaration.fromClass(it.clazz))

                VertexASMLog.log(log, "INVOKESPECIAL $className $initFunctionName $functionDescriptor")
            }

            is DefineFunctionInvocation -> {
                // GET_FIELD of this instance will use 1 more load instruction (ALOAD 0)
                if (it.parameters.size != (it.args.size - it.args.count { it is LoadFieldValue })) {
                    throw IllegalFunctionAccessException("Trying call ${it.methodName}() of ${it.owner.canonicalName}" +
                        " but count of args(${it.args.size}) is not equals to parameters(${it.parameters.size})")
                }

                val owner = ASMUtils.getInternalName(it.owner)
                val methodName = it.methodName
                val descriptor = it.getDescriptor()

                // Prepare parameters
                it.args.forEach {
                    processLoadValue(methodWriter, method, methodLocalStack, methodLocalVariableMap, it)
                }

                // Invoke function
                val instruction = when (it.type) {
                    FunctionInvocationType.NORMAL -> JVMInstruction.INVOKEVIRTUAL

                    FunctionInvocationType.PRIVATE,
                    FunctionInvocationType.SUPER -> JVMInstruction.INVOKESPECIAL

                    FunctionInvocationType.INTERFACE -> JVMInstruction.INVOKEINTERFACE

                    FunctionInvocationType.STATIC -> JVMInstruction.INVOKESTATIC
                }

                methodWriter.visitMethodInsn(
                    instruction.code,
                    owner,
                    methodName,
                    descriptor,
                    false
                )

                for (i in 0..<it.args.size) {
                    methodLocalStack.pop()
                }

                if (it.hasReturnType()) {
                    methodLocalStack.push(it.returnType)
                }

                VertexASMLog.log(log, "${instruction.name} $owner $methodName $descriptor")
            }

            is DefineReturn -> {
                val instruction = ASMUtils.getReturnOpcode(method.returnType?.originalClass ?: Void::class.java)
                methodWriter.visitInsn(instruction.code)
                VertexASMLog.log(log, instruction.name)
            }

            is DefineTypeCast -> {
                val instruction = JVMInstruction.CHECKCAST
                val targetInternalName = it.target.getInternalClassName()
                methodWriter.visitTypeInsn(instruction.code, targetInternalName)
                VertexASMLog.log(log, "${instruction.name} $targetInternalName")
            }
        }
    }
}