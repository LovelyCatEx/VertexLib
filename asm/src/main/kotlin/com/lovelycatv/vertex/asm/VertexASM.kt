package com.lovelycatv.vertex.asm

import com.lovelycatv.vertex.asm.lang.ClassDeclaration
import com.lovelycatv.vertex.asm.loader.ByteClassLoader
import com.lovelycatv.vertex.asm.processor.clazz.ClassProcessor

/**
 * @author lovelycat
 * @since 2025-08-01 18:47
 * @version 1.0
 */
object VertexASM {
    fun writeClassToByteArray(classDeclaration: ClassDeclaration): ByteArray {
        val cp = ClassProcessor()
        cp.writeClass(classDeclaration)
        return cp.classWriter.toByteArray()
    }

    fun loadClassFromByteArray(className: String, code: ByteArray): Class<*> {
        val loader = ByteClassLoader(className, code)
        return loader.loadClass(className)
    }
}