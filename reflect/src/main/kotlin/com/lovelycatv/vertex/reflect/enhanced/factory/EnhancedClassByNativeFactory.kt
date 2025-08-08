package com.lovelycatv.vertex.reflect.enhanced.factory

import com.lovelycatv.vertex.reflect.TypeUtils
import com.lovelycatv.vertex.reflect.enhanced.EnhancedClassByNative
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileOutputStream

/**
 * @author lovelycat
 * @since 2025-08-08 15:34
 * @version 1.0
 */
class EnhancedClassByNativeFactory : EnhancedClassFactory<EnhancedClassByNative>(EnhancedClassByNative::class.java) {
    override fun internalCreate(classWriter: ClassWriter, targetClass: Class<*>) {
        switchBasedInvokeMethodGenerator(classWriter, targetClass) { method ->
            visitVarInsn(Opcodes.ALOAD, 1) // target
            visitTypeInsn(Opcodes.CHECKCAST, TypeUtils.getInternalName(targetClass))

            method.parameterTypes.forEachIndexed { index, parameterClass ->
                visitVarInsn(Opcodes.ALOAD, 3)
                visitIntInsn(Opcodes.BIPUSH, index)
                visitInsn(Opcodes.AALOAD)

                if (TypeUtils.isPrimitiveType(parameterClass)) {
                    // Cast to packaged type
                    val packagedClass = TypeUtils.getPackagedPrimitiveType(parameterClass)

                    visitTypeInsn(Opcodes.CHECKCAST, TypeUtils.getInternalName(packagedClass))

                    visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        TypeUtils.getInternalName(packagedClass),
                        "${parameterClass.canonicalName}Value",
                        "()${TypeUtils.getDescriptor(parameterClass)}",
                        false
                    )

                }
            }

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
    }

}