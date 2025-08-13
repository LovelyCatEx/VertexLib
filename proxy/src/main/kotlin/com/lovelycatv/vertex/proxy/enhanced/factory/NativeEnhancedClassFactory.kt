package com.lovelycatv.vertex.proxy.enhanced.factory

import com.lovelycatv.vertex.reflect.ReflectUtils
import com.lovelycatv.vertex.reflect.TypeUtils
import com.lovelycatv.vertex.proxy.enhanced.NativeEnhancedClass
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author lovelycat
 * @since 2025-08-08 15:34
 * @version 1.0
 */
class NativeEnhancedClassFactory : EnhancedClassFactory<NativeEnhancedClass>(NativeEnhancedClass::class.java) {
    override fun internalCreate(classWriter: ClassWriter, targetClass: Class<*>) {

    }

    override fun internalCreateInvokeMethod(methodVisitor: MethodVisitor, targetClass: Class<*>) {
        val fxLoadFromArgs = fun(mv: MethodVisitor, parameterTypes: Array<Class<*>>) {
            parameterTypes.forEachIndexed { index, parameterClass ->
                mv.visitVarInsn(Opcodes.ALOAD, 3) // args
                mv.visitIntInsn(Opcodes.BIPUSH, index)
                mv.visitInsn(Opcodes.AALOAD)

                if (TypeUtils.isPrimitiveType(parameterClass)) {
                    // Cast to packaged type
                    val packagedClass = TypeUtils.getPackagedPrimitiveType(parameterClass)

                    mv.visitTypeInsn(Opcodes.CHECKCAST, TypeUtils.getInternalName(packagedClass))

                    mv.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        TypeUtils.getInternalName(packagedClass),
                        "${parameterClass.canonicalName}Value",
                        "()${TypeUtils.getDescriptor(parameterClass)}",
                        false
                    )
                } else {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, TypeUtils.getInternalName(parameterClass))
                }
            }
        }

        switchBasedInvokeMethodGenerator(
            methodVisitor,
            targetClass,
            onConstructorBranch = { constructor ->
                val classInternalName = TypeUtils.getInternalName(targetClass)
                visitTypeInsn(Opcodes.NEW, classInternalName)
                visitInsn(Opcodes.DUP)

                fxLoadFromArgs.invoke(methodVisitor, constructor.parameterTypes)

                visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    classInternalName,
                    ReflectUtils.CONSTRUCTOR_NAME,
                    constructor.parameterTypes.joinToString(separator = "", prefix = "(", postfix = ")V") {
                        TypeUtils.getDescriptor(it)
                    },
                    false
                )

                visitInsn(Opcodes.ARETURN)
            },
            onMethodBranch = { method ->
                visitVarInsn(Opcodes.ALOAD, 1) // target
                visitTypeInsn(Opcodes.CHECKCAST, TypeUtils.getInternalName(targetClass))

                fxLoadFromArgs.invoke(methodVisitor, method.parameterTypes)

                visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    TypeUtils.getInternalName(targetClass),
                    method.name,
                    TypeUtils.getMethodDescriptor(method),
                    false
                )

                if (!TypeUtils.isVoid(method.returnType)) {
                    val tReturnType = method.returnType

                    if (TypeUtils.isPrimitiveType(tReturnType)) {
                        // Cast to packaged type
                        val packagedClass = TypeUtils.getPackagedPrimitiveType(tReturnType)

                        visitMethodInsn(
                            Opcodes.INVOKESTATIC,
                            TypeUtils.getInternalName(packagedClass),
                            "valueOf",
                            "(${TypeUtils.getDescriptor(tReturnType)})${TypeUtils.getDescriptor(packagedClass)}",
                            false
                        )
                    }

                    visitInsn(Opcodes.ARETURN)
                } else {
                    visitInsn(Opcodes.ACONST_NULL)
                    visitInsn(Opcodes.ARETURN)
                }
            }
        )
    }

}