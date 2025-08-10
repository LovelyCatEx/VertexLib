package com.lovelycatv.vertex.reflect.enhanced.factory

import com.lovelycatv.vertex.reflect.TypeUtils
import com.lovelycatv.vertex.reflect.enhanced.EnhancedClass
import com.lovelycatv.vertex.reflect.loader.ByteClassLoader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * @author lovelycat
 * @since 2025-08-08 15:32
 * @version 1.0
 */
abstract class EnhancedClassFactory<R: EnhancedClass>(
    val parentEnhancedClass: Class<out EnhancedClass>,
    private val enhancedClassName: ((Class<*>) -> String) = { "Enhanced${it.simpleName}" }
) {
    fun create(targetClass: Class<*>): R {
        val className = enhancedClassName.invoke(targetClass)
        val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)

        cw.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC,
            className,
            null,
            TypeUtils.getInternalName(parentEnhancedClass),
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
            TypeUtils.getInternalName(this.parentEnhancedClass),
            "<init>",
            "(${TypeUtils.getDescriptor(Class::class.java)})V",
            false
        )
        primaryConstructor.visitInsn(Opcodes.RETURN)
        primaryConstructor.visitMaxs(-1, -1)
        primaryConstructor.visitEnd()

        this.internalCreate(cw, targetClass)

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

        this.internalCreateInvokeMethod(invokeImpl, targetClass)

        invokeImpl.visitMaxs(-1, -1)
        invokeImpl.visitEnd()

        cw.visitEnd()

        @Suppress("UNCHECKED_CAST")
        return ByteClassLoader(className, cw.toByteArray(), EnhancedClass::class.java.classLoader)
            .loadClass(className)
            .constructors.first()
            .newInstance(targetClass) as R
    }

    protected abstract fun internalCreate(classWriter: ClassWriter, targetClass: Class<*>)

    protected abstract fun internalCreateInvokeMethod(methodVisitor: MethodVisitor, targetClass: Class<*>)

    companion object {
        @JvmStatic
        protected fun switchBasedInvokeMethodGenerator(
            invokeImpl: MethodVisitor,
            targetClass: Class<*>,
            onConstructorBranch: MethodVisitor.(constructor: Constructor<*>) -> Unit,
            onMethodBranch: MethodVisitor.(method: Method) -> Unit
        ) {
            val constructors = targetClass.constructors
            val qualifiedMethods = targetClass.declaredMethods.filter { !Modifier.isFinal(it.modifiers) && !it.isSynthetic }

            // Index of MethodHandle
            invokeImpl.visitVarInsn(Opcodes.ILOAD, 2) // index

            val methodCount = constructors.size + qualifiedMethods.size
            val defaultLabel = Label()
            val labels: Array<Label?> = arrayOfNulls(methodCount)
            for (i in 0..<methodCount) {
                labels[i] = Label()
            }
            invokeImpl.visitTableSwitchInsn(0, methodCount - 1, defaultLabel, *labels)
            for (i in constructors.indices) {
                invokeImpl.visitLabel(labels[i])

                val currentConstructor = constructors[i]
                onConstructorBranch.invoke(invokeImpl, currentConstructor)
            }
            for (i in qualifiedMethods.indices) {
                invokeImpl.visitLabel(labels[i + constructors.size])

                val currentMethod = qualifiedMethods[i]
                onMethodBranch.invoke(invokeImpl, currentMethod)
            }

            invokeImpl.visitLabel(defaultLabel)
            invokeImpl.visitTypeInsn(Opcodes.NEW, "java/lang/IllegalArgumentException")
            invokeImpl.visitInsn(Opcodes.DUP)
            invokeImpl.visitLdcInsn("Invalid method index")
            invokeImpl.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
            invokeImpl.visitInsn(Opcodes.ATHROW)
        }
    }
}