package com.lovelycatv.vertex.reflect.enhanced

import com.lovelycatv.vertex.reflect.TypeUtils
import com.lovelycatv.vertex.reflect.loader.ByteClassLoader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import java.lang.invoke.MethodHandle
import java.lang.reflect.Modifier


/**
 * @author lovelycat
 * @since 2025-08-08 11:52
 * @version 1.0
 */
class EnhancedClassFactory {
    companion object {
        val INSTANCE = EnhancedClassFactory()
    }

    fun create(
        targetClass: Class<*>,
        enhancedClassName: ((Class<*>) -> String) = { "Enhanced${targetClass.simpleName}" }
    ): EnhancedClass {
        val qualifiedMethods = targetClass.declaredMethods.filter { !Modifier.isFinal(it.modifiers) && !it.isSynthetic }

        val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)

        val implClassName = enhancedClassName.invoke(targetClass)

        cw.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC,
            implClassName,
            null,
            TypeUtils.getInternalName(EnhancedClass::class.java),
            null
        )

        // Constructor
        val primaryConstructor = cw.visitMethod(
            Opcodes.ACC_PUBLIC,
            "<init>",
            "(${TypeUtils.getDescriptor(Class::class.java)})V",
            null,
            null
        )
        primaryConstructor.visitVarInsn(Opcodes.ALOAD, 0)
        primaryConstructor.visitVarInsn(Opcodes.ALOAD, 1)
        primaryConstructor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            TypeUtils.getInternalName(EnhancedClass::class.java),
            "<init>",
            "(${TypeUtils.getDescriptor(Class::class.java)})V",
            false
        )
        primaryConstructor.visitInsn(Opcodes.RETURN)
        primaryConstructor.visitMaxs(-1, -1)
        primaryConstructor.visitEnd()

        // Implementations
        val invokeImpl = cw.visitMethod(
            Opcodes.ACC_PUBLIC,
            "invokeMethod",
                "(${TypeUtils.getDescriptor(Any::class.java)}" +
                    TypeUtils.getDescriptor(Int::class.java) +
                    TypeUtils.getArrayDescriptor(Any::class.java, 1) +
                ")${TypeUtils.getDescriptor(Any::class.java)}",
            null,
            null
        )

        invokeImpl.visitVarInsn(Opcodes.ALOAD, 0)

        // Index of MethodHandle
        invokeImpl.visitVarInsn(Opcodes.ILOAD, 2) // index

        val methodCount = qualifiedMethods.size
        val defaultLabel = Label()
        val labels: Array<Label?> = arrayOfNulls(methodCount)
        for (i in 0..<methodCount) {
            labels[i] = Label()
        }
        invokeImpl.visitTableSwitchInsn(0, methodCount - 1, defaultLabel, *labels)
        for (i in 0..<methodCount) {
            val currentMethod = qualifiedMethods[i]

            invokeImpl.visitLabel(labels[i])

            // this.methodHandles[i]
            invokeImpl.visitVarInsn(Opcodes.ALOAD, 0)
            invokeImpl.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                TypeUtils.getInternalName(EnhancedClass::class.java),
                "getMethodHandles",
                "()${TypeUtils.getArrayDescriptor(MethodHandle::class.java, 1)}",
                false
            )
            invokeImpl.visitVarInsn(Opcodes.ILOAD, 2) // index
            invokeImpl.visitInsn(Opcodes.AALOAD)

            invokeImpl.visitVarInsn(Opcodes.ALOAD, 1)

            for (j in 0..<currentMethod.parameterCount) {
                invokeImpl.visitVarInsn(Opcodes.ALOAD, 3)
                invokeImpl.visitIntInsn(Opcodes.BIPUSH, j)
                invokeImpl.visitInsn(Opcodes.AALOAD)
            }

            invokeImpl.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/invoke/MethodHandle",
                "invoke",
                "(${"Ljava/lang/Object;".repeat(1 + currentMethod.parameterCount)})Ljava/lang/Object;",
                false
            )

            invokeImpl.visitInsn(Opcodes.ARETURN)
        }

        invokeImpl.visitLabel(defaultLabel)
        invokeImpl.visitTypeInsn(Opcodes.NEW, "java/lang/IllegalArgumentException")
        invokeImpl.visitInsn(Opcodes.DUP)
        invokeImpl.visitLdcInsn("Invalid method index")
        invokeImpl.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
        invokeImpl.visitInsn(Opcodes.ATHROW)

        invokeImpl.visitMaxs(-1, -1)
        invokeImpl.visitEnd()

        cw.visitEnd()

        return ByteClassLoader(implClassName, cw.toByteArray(), EnhancedClass::class.java.classLoader)
            .loadClass(implClassName)
            .constructors.first()
            .newInstance(targetClass) as EnhancedClass
    }
}