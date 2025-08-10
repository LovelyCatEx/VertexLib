package com.lovelycatv.vertex.reflect.enhanced.factory

import com.lovelycatv.vertex.reflect.TypeUtils
import com.lovelycatv.vertex.reflect.enhanced.JavaEnhancedClass
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.lang.invoke.MethodHandle
import java.lang.reflect.Executable


/**
 * @author lovelycat
 * @since 2025-08-08 11:52
 * @version 1.0
 */
class JavaEnhancedClassFactory : EnhancedClassFactory<JavaEnhancedClass>(JavaEnhancedClass::class.java) {
    override fun internalCreate(classWriter: ClassWriter, targetClass: Class<*>) {

    }

    override fun internalCreateInvokeMethod(methodVisitor: MethodVisitor, targetClass: Class<*>) {
        val fx = fun (mv: MethodVisitor, ex: Executable, isConstructor: Boolean) {
            // this.methodHandles[i]
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                TypeUtils.getInternalName(JavaEnhancedClass::class.java),
                if (isConstructor) "getConstructors" else "getMethodHandles",
                "()${TypeUtils.getArrayDescriptor(MethodHandle::class.java, 1)}",
                false
            )
            mv.visitVarInsn(Opcodes.ILOAD, 2) // index
            mv.visitInsn(Opcodes.AALOAD)

            if (!isConstructor) {
                mv.visitVarInsn(Opcodes.ALOAD, 1)
            }

            ex.parameterTypes.forEachIndexed { index, _ ->
                mv.visitVarInsn(Opcodes.ALOAD, 3)
                mv.visitIntInsn(Opcodes.BIPUSH, index)
                mv.visitInsn(Opcodes.AALOAD)
            }

            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/invoke/MethodHandle",
                "invoke",
                "(${"Ljava/lang/Object;".repeat((if (!isConstructor) 1 else 0) + ex.parameterCount)})Ljava/lang/Object;",
                false
            )

            mv.visitInsn(Opcodes.ARETURN)
        }

        switchBasedInvokeMethodGenerator(
            methodVisitor,
            targetClass,
            onConstructorBranch = { fx.invoke(this, it, true) },
            onMethodBranch = { fx.invoke(this, it, false) }
        )
    }
}