package com.lovelycatv.vertex.asm.lang.code

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.lang.FieldDeclaration
import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.asm.lang.code.calculate.CalculateType
import com.lovelycatv.vertex.asm.lang.code.calculate.Calculation
import com.lovelycatv.vertex.asm.lang.code.define.*
import com.lovelycatv.vertex.asm.lang.code.load.*
import com.lovelycatv.vertex.asm.lang.code.store.PopValue
import com.lovelycatv.vertex.asm.lang.code.store.StoreArrayValue
import com.lovelycatv.vertex.asm.lang.code.store.StoreFieldVariable
import com.lovelycatv.vertex.asm.lang.code.store.StoreLocalVariable
import kotlin.reflect.*
import kotlin.reflect.jvm.javaField

/**
 * @author lovelycat
 * @since 2025-08-02 15:39
 * @version 1.0
 */
class CodeWriter(private val onCodeWritten: (IJavaCode) -> Unit) {
    fun defineVariable(name: String, type: TypeDeclaration): DefineLocalVariable {
        return DefineLocalVariable(type, name, false).also {
            onCodeWritten.invoke(it)
        }
    }
    fun defineVariable(name: String, type: Class<*>): DefineLocalVariable {
        return DefineLocalVariable(TypeDeclaration.fromClass(type), name, false).also {
            onCodeWritten.invoke(it)
        }
    }

    fun defineFinalVariable(name: String, type: TypeDeclaration): DefineLocalVariable {
        return DefineLocalVariable(type, name, true).also {
            onCodeWritten.invoke(it)
        }
    }

    fun defineFinalVariable(name: String, type: Class<*>): DefineLocalVariable {
        return DefineLocalVariable(TypeDeclaration.fromClass(type), name, true).also {
            onCodeWritten.invoke(it)
        }
    }

    fun typeCast(target: TypeDeclaration): DefineTypeCast {
        return DefineTypeCast(target).also {
            onCodeWritten.invoke(it)
        }
    }

    fun primitiveTypeCast(from: TypeDeclaration, to: TypeDeclaration): DefineTypeCast {
        return DefineTypeCast(to, from).also {
            onCodeWritten.invoke(it)
        }
    }


    fun loadThis(): LoadThis {
        return LoadThis().also {
            onCodeWritten.invoke(it)
        }
    }

    fun loadVariable(variableName: String): LoadLocalVariable {
        return LoadLocalVariable(variableName).also {
            onCodeWritten.invoke(it)
        }
    }

    fun loadConstant(value: Any?): LoadConstantValue {
        return LoadConstantValue(value).also {
            onCodeWritten.invoke(it)
        }
    }

    fun loadMethodParameter(index: Int): LoadMethodParameter {
        return LoadMethodParameter(index).also {
            onCodeWritten.invoke(it)
        }
    }

    fun loadField(targetClass: Class<*>?, fieldName: String, fieldType: Class<*>): LoadFieldValue {
        if (targetClass == null) {
            loadThis()
        }

        return LoadFieldValue(targetClass, fieldName, fieldType, false).also {
            onCodeWritten.invoke(it)
        }
    }

    fun loadField(targetClass: Class<*>?, field: FieldDeclaration): LoadFieldValue {
        return this.loadField(targetClass, field.name, field.type.originalClass)
    }


    fun loadStaticField(targetClass: Class<*>?, fieldName: String, fieldType: Class<*>): LoadFieldValue {
        return LoadFieldValue(targetClass, fieldName, fieldType, true).also {
            onCodeWritten.invoke(it)
        }
    }

    fun loadStaticField(targetClass: Class<*>?, field: FieldDeclaration): LoadFieldValue {
        return this.loadStaticField(targetClass, field.name, field.type.originalClass)
    }

    fun loadStaticField(target: KProperty0<*>): LoadFieldValue {
        val fieldName = target.name

        val owner = target.javaField?.declaringClass
            ?: throw IllegalStateException("Could not access the target because of the owner is unrecognized")
        val field = owner.getDeclaredField(fieldName)

        return LoadFieldValue(owner, target.name, field.type, true).also {
            onCodeWritten.invoke(it)
        }
    }

    fun loadNull(): LoadNull {
        return LoadNull().also {
            onCodeWritten.invoke(it)
        }
    }

    fun loadArray(type: TypeDeclaration, dimensions: Int, lengths: Array<Int>): LoadArray {
        return LoadArray(type, dimensions, lengths).also {
            onCodeWritten.invoke(it)
        }
    }

    fun loadArrayValue(elementType: TypeDeclaration, index: CodeWriter.() -> Unit): LoadArrayValue {
        val t = mutableListOf<IJavaCode>()
        val writer = CodeWriter { t.add(it) }
        index.invoke(writer)

        return LoadArrayValue(elementType, t.toTypedArray()).also {
            onCodeWritten.invoke(it)
        }
    }

    fun storeVariable(variableName: String): StoreLocalVariable {
        return StoreLocalVariable(variableName).also {
            onCodeWritten.invoke(it)
        }
    }

    fun storeField(
        targetClass: Class<*>?,
        field: FieldDeclaration,
        isStatic: Boolean,
        loader: CodeWriter.() -> Unit
    ): StoreFieldVariable {
        return this.storeField(targetClass, field.name, field.type, isStatic, loader)
    }

    fun storeField(
        targetClass: Class<*>?,
        fieldName: String,
        fieldType: TypeDeclaration,
        isStatic: Boolean,
        loader: CodeWriter.() -> Unit
    ): StoreFieldVariable {
        if (targetClass == null && !isStatic) {
            loadThis()
        }

        val cw = CodeWriter {
            onCodeWritten.invoke(it)
        }
        loader.invoke(cw)

        return StoreFieldVariable(targetClass, fieldName, fieldType, isStatic).also {
            onCodeWritten.invoke(it)
        }
    }

    fun storeArrayValue(
        elementType: TypeDeclaration,
        index: CodeWriter.() -> Unit,
        newValue: (CodeWriter.() -> Unit)? = null
    ): StoreArrayValue {
        val t = mutableListOf<IJavaCode>()
        val writer = CodeWriter { t.add(it) }
        index.invoke(writer)

        val indexLoader = arrayOf(*t.toTypedArray())
        t.clear()

        newValue?.invoke(writer)
        val newValueLoaders = if (t.isEmpty()) {
            arrayOf(LoadNull())
        } else {
            t.toTypedArray()
        }

        return StoreArrayValue(elementType, indexLoader, newValueLoaders).also {
            onCodeWritten.invoke(it)
        }
    }


    fun newInstance(
        clazz: Class<*>,
        constructorParameters: Array<TypeDeclaration> = arrayOf(),
        argsWriter: (CodeWriter.() -> Unit)? = null
    ): DefineNewInstance {
        val t = mutableListOf<IJavaCode>()
        val writer = CodeWriter { t.add(it) }
        argsWriter?.invoke(writer)

        return DefineNewInstance(
            clazz = clazz,
            constructorParameters = constructorParameters,
            args = t.toTypedArray()
        ).also {
            onCodeWritten.invoke(it)
        }
    }

    fun invokeMethod(
        type: FunctionInvocationType = FunctionInvocationType.NORMAL,
        owner: Class<*>,
        methodName: String,
        parameters: Array<TypeDeclaration> = arrayOf(),
        returnType: Class<*> = ASMUtils.OBJECT_CLASS,
        argsWriter: (CodeWriter.() -> Unit)? = null
    ): DefineFunctionInvocation {
        return this.invokeMethod(type, owner, methodName, parameters, TypeDeclaration.fromClass(returnType), argsWriter)
    }

    fun invokeMethod(
        type: FunctionInvocationType = FunctionInvocationType.NORMAL,
        owner: Class<*>,
        methodName: String,
        parameters: Array<TypeDeclaration> = arrayOf(),
        returnType: TypeDeclaration = TypeDeclaration.VOID,
        argsWriter: (CodeWriter.() -> Unit)? = null
    ): DefineFunctionInvocation {
        val t = mutableListOf<IJavaCode>()
        val writer = CodeWriter { t.add(it) }
        argsWriter?.invoke(writer)

        return DefineFunctionInvocation(
            type = type,
            owner = owner,
            methodName = methodName,
            parameters = parameters,
            returnType = returnType,
            args = t.toTypedArray()
        ).also {
            onCodeWritten.invoke(it)
        }
    }

    @Deprecated("This method has serious performance problem.")
    @OptIn(ExperimentalStdlibApi::class)
    fun invokeMethod(
        type: FunctionInvocationType = FunctionInvocationType.NORMAL,
        function: KCallable<*>,
        argsWriter: (CodeWriter.() -> Unit)? = null
    ): DefineFunctionInvocation {
        val funcParameters = function.parameters
        val returnType = function.returnType.javaType as Class<*>
        val funcName = function.name

        val targetClass = funcParameters[0].type.javaType as Class<*>
        val parameters = funcParameters.drop(1).map { TypeDeclaration.fromClass(it.type.javaType as Class<*>) }.toTypedArray()

        return this.invokeMethod(
            type = type,
            owner = targetClass,
            methodName = funcName,
            parameters = parameters,
            returnType = returnType,
            argsWriter = argsWriter
        )
    }

    fun returnFunc(): DefineReturn {
        return DefineReturn().also {
            onCodeWritten.invoke(it)
        }
    }

    fun pop(count: Int = 1): PopValue {
        return PopValue(count).also {
            onCodeWritten.invoke(it)
        }
    }

    fun add(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.ADD, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun intAdd(): Calculation {
        return this.add(TypeDeclaration.INT)
    }

    fun longAdd(): Calculation {
        return this.add(TypeDeclaration.LONG)
    }

    fun floatAdd(): Calculation {
        return this.add(TypeDeclaration.FLOAT)
    }

    fun doubleAdd(): Calculation {
        return this.add(TypeDeclaration.DOUBLE)
    }

    fun subtract(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.SUBTRACT, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun intSubtract(): Calculation {
        return this.subtract(TypeDeclaration.INT)
    }

    fun longSubtract(): Calculation {
        return this.subtract(TypeDeclaration.LONG)
    }

    fun floatSubtract(): Calculation {
        return this.subtract(TypeDeclaration.FLOAT)
    }

    fun doubleSubtract(): Calculation {
        return this.subtract(TypeDeclaration.DOUBLE)
    }

    fun multiply(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.MULTIPLY, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun intMultiply(): Calculation {
        return this.multiply(TypeDeclaration.INT)
    }

    fun longMultiply(): Calculation {
        return this.multiply(TypeDeclaration.LONG)
    }

    fun floatMultiply(): Calculation {
        return this.multiply(TypeDeclaration.FLOAT)
    }

    fun doubleMultiply(): Calculation {
        return this.multiply(TypeDeclaration.DOUBLE)
    }

    fun divide(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.DIVIDE, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun intDivide(): Calculation {
        return this.divide(TypeDeclaration.INT)
    }

    fun longDivide(): Calculation {
        return this.divide(TypeDeclaration.LONG)
    }

    fun floatDivide(): Calculation {
        return this.divide(TypeDeclaration.FLOAT)
    }

    fun doubleDivide(): Calculation {
        return this.divide(TypeDeclaration.DOUBLE)
    }

    fun rem(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.REM, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun intRem(): Calculation {
        return this.rem(TypeDeclaration.INT)
    }

    fun longRem(): Calculation {
        return this.rem(TypeDeclaration.LONG)
    }

    fun floatRem(): Calculation {
        return this.rem(TypeDeclaration.FLOAT)
    }

    fun doubleRem(): Calculation {
        return this.rem(TypeDeclaration.DOUBLE)
    }

    fun negative(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.NEGATIVE, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun intNegative(): Calculation {
        return this.negative(TypeDeclaration.INT)
    }

    fun longNegative(): Calculation {
        return this.negative(TypeDeclaration.LONG)
    }

    fun floatNegative(): Calculation {
        return this.negative(TypeDeclaration.FLOAT)
    }

    fun doubleNegative(): Calculation {
        return this.negative(TypeDeclaration.DOUBLE)
    }

    fun shl(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.SHIFT_LEFT, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun intShl(): Calculation {
        return this.shl(TypeDeclaration.INT)
    }

    fun longShl(): Calculation {
        return this.shl(TypeDeclaration.LONG)
    }

    fun shr(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.SHIFT_RIGHT, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun intShr(): Calculation {
        return this.shr(TypeDeclaration.INT)
    }

    fun longShr(): Calculation {
        return this.shr(TypeDeclaration.LONG)
    }

    fun unsignedShr(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.U_SHIFT_RIGHT, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun unsignedIntShr(): Calculation {
        return this.unsignedShr(TypeDeclaration.INT)
    }

    fun unsignedLongShr(): Calculation {
        return this.unsignedShr(TypeDeclaration.LONG)
    }

    fun and(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.AND, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun or(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.OR, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun xor(numberType: TypeDeclaration): Calculation {
        return Calculation(type = CalculateType.XOR, numberType = numberType).also {
            onCodeWritten.invoke(it)
        }
    }

    fun intAnd(): Calculation {
        return this.and(TypeDeclaration.INT)
    }

    fun intOr(): Calculation {
        return this.or(TypeDeclaration.INT)
    }

    fun intXor(): Calculation {
        return this.xor(TypeDeclaration.INT)
    }

    fun longAnd(): Calculation {
        return this.and(TypeDeclaration.LONG)
    }

    fun longOr(): Calculation {
        return this.or(TypeDeclaration.LONG)
    }

    fun longXor(): Calculation {
        return this.xor(TypeDeclaration.LONG)
    }
}