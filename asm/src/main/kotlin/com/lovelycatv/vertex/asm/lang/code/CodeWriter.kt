package com.lovelycatv.vertex.asm.lang.code

import com.lovelycatv.vertex.asm.ASMUtils
import com.lovelycatv.vertex.asm.lang.TypeDeclaration
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
class CodeWriter(private val onCodeWritten: ((IJavaCode) -> Unit)? = null) {
    fun defineVariable(name: String, type: TypeDeclaration): DefineLocalVariable {
        return DefineLocalVariable(type, name, false).also {
            onCodeWritten?.invoke(it)
        }
    }
    fun defineVariable(name: String, type: Class<*>): DefineLocalVariable {
        return DefineLocalVariable(TypeDeclaration.fromClass(type), name, false).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun defineFinalVariable(name: String, type: TypeDeclaration): DefineLocalVariable {
        return DefineLocalVariable(type, name, true).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun defineFinalVariable(name: String, type: Class<*>): DefineLocalVariable {
        return DefineLocalVariable(TypeDeclaration.fromClass(type), name, true).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun typeCast(target: TypeDeclaration): DefineTypeCast {
        return DefineTypeCast(target).also {
            onCodeWritten?.invoke(it)
        }
    }


    fun loadThis(): LoadThis {
        return LoadThis().also {
            onCodeWritten?.invoke(it)
        }
    }

    fun loadVariable(variableName: String): LoadLocalVariable {
        return LoadLocalVariable(variableName).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun loadConstant(value: Any?): LoadConstantValue {
        return LoadConstantValue(value).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun loadMethodParameter(index: Int): LoadMethodParameter {
        return LoadMethodParameter(index).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun loadField(targetClass: Class<*>?, fieldName: String, fieldType: Class<*>): LoadFieldValue {
        if (targetClass == null) {
            loadThis()
        }

        return LoadFieldValue(targetClass, fieldName, fieldType, false).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun loadStaticField(targetClass: Class<*>?, fieldName: String, fieldType: Class<*>): LoadFieldValue {
        return LoadFieldValue(targetClass, fieldName, fieldType, true).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun loadStaticField(target: KProperty0<*>): LoadFieldValue {
        val fieldName = target.name

        val owner = target.javaField?.declaringClass
            ?: throw IllegalStateException("Could not access the target because of the owner is unrecognized")
        val field = owner.getDeclaredField(fieldName)

        return LoadFieldValue(owner, target.name, field.type, true).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun loadNull(): LoadNull {
        return LoadNull().also {
            onCodeWritten?.invoke(it)
        }
    }

    fun loadArray(type: TypeDeclaration, dimensions: Int, lengths: Array<Int>): LoadArray {
        return LoadArray(type, dimensions, lengths).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun loadArrayValue(elementType: TypeDeclaration, index: CodeWriter.() -> Unit): LoadArrayValue {
        val t = mutableListOf<IJavaCode>()
        val writer = CodeWriter { t.add(it) }
        index.invoke(writer)

        return LoadArrayValue(elementType, t.toTypedArray()).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun storeVariable(variableName: String): StoreLocalVariable {
        return StoreLocalVariable(variableName).also {
            onCodeWritten?.invoke(it)
        }
    }

    fun storeField(
        targetClass: Class<*>?,
        fieldName: String,
        fieldType: TypeDeclaration,
        isStatic: Boolean,
        loader: CodeWriter.() -> Unit
    ): StoreFieldVariable {
        if (targetClass == null) {
            loadThis()
        }

        val cw = CodeWriter {
            onCodeWritten?.invoke(it)
        }
        loader.invoke(cw)

        return StoreFieldVariable(targetClass, fieldName, fieldType, isStatic).also {
            onCodeWritten?.invoke(it)
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
            onCodeWritten?.invoke(it)
        }
    }


    fun newInstance(
        clazz: Class<*>,
        constructorParameters: Array<TypeDeclaration> = arrayOf(),
        argsWriter: (CodeWriter.() -> Unit)? = null
    ): DefineNewInstance {
        val t = mutableListOf<ILoadValue>()
        val writer = CodeWriter { if (it is ILoadValue) t.add(it) }
        argsWriter?.invoke(writer)

        return DefineNewInstance(
            clazz = clazz,
            constructorParameters = constructorParameters,
            args = t.toTypedArray()
        ).also {
            onCodeWritten?.invoke(it)
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
        val t = mutableListOf<ILoadValue>()
        val writer = CodeWriter { if (it is ILoadValue) t.add(it) }
        argsWriter?.invoke(writer)

        return DefineFunctionInvocation(
            type = type,
            owner = owner,
            methodName = methodName,
            parameters = parameters,
            returnType = returnType,
            args = t.toTypedArray()
        ).also {
            onCodeWritten?.invoke(it)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun invokeMethod(
        type: FunctionInvocationType = FunctionInvocationType.NORMAL,
        function: KCallable<*>,
        argsWriter: (CodeWriter.() -> Unit)? = null
    ): DefineFunctionInvocation {
        val funcParameters = function.parameters
        val returnType = Class.forName(function.returnType.javaType.typeName)
        val funcName = function.name

        val targetClass = Class.forName(funcParameters[0].type.javaType.typeName)
        val parameters = funcParameters.drop(1).map { TypeDeclaration.fromName(it.type.javaType.typeName) }.toTypedArray()

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
            onCodeWritten?.invoke(it)
        }
    }

    fun pop(count: Int = 1): PopValue {
        return PopValue(count).also {
            onCodeWritten?.invoke(it)
        }
    }
}