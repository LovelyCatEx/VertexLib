package com.lovelycatv.vertex.reflect.enhanced.factory

import com.lovelycatv.vertex.reflect.TypeUtils
import com.lovelycatv.vertex.reflect.enhanced.JavaEnhancedClass
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.lang.invoke.MethodHandle


/**
 * @author lovelycat
 * @since 2025-08-08 11:52
 * @version 1.0
 */
class JavaEnhancedClassFactory : EnhancedClassFactory<JavaEnhancedClass>(JavaEnhancedClass::class.java) {
    override fun internalCreate(classWriter: ClassWriter, targetClass: Class<*>) {

    }

    override fun internalCreateInvokeMethod(methodVisitor: MethodVisitor, targetClass: Class<*>) {
        switchBasedInvokeMethodGenerator(methodVisitor, targetClass) { method ->
            // this.methodHandles[i]
            visitVarInsn(Opcodes.ALOAD, 0)
            visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                TypeUtils.getInternalName(JavaEnhancedClass::class.java),
                "getMethodHandles",
                "()${TypeUtils.getArrayDescriptor(MethodHandle::class.java, 1)}",
                false
            )
            visitVarInsn(Opcodes.ILOAD, 2) // index
            visitInsn(Opcodes.AALOAD)

            visitVarInsn(Opcodes.ALOAD, 1)

            method.parameterTypes.forEachIndexed { index, _ ->
                visitVarInsn(Opcodes.ALOAD, 3)
                visitIntInsn(Opcodes.BIPUSH, index)
                visitInsn(Opcodes.AALOAD)
            }


            visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/invoke/MethodHandle",
                "invoke",
                "(${"Ljava/lang/Object;".repeat(1 + method.parameterCount)})Ljava/lang/Object;",
                false
            )

            visitInsn(Opcodes.ARETURN)
        }
    }
}