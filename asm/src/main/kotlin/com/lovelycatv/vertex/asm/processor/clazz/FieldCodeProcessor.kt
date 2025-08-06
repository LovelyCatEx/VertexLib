package com.lovelycatv.vertex.asm.processor.clazz

import com.lovelycatv.vertex.asm.VertexASMLog
import com.lovelycatv.vertex.asm.lang.FieldDeclaration
import com.lovelycatv.vertex.asm.visitField
import com.lovelycatv.vertex.log.logger

/**
 * @author lovelycat
 * @since 2025-08-06 16:58
 * @version 1.0
 */
class FieldCodeProcessor(private val context: ClassProcessor.Context) {
    private val log = logger()

    fun processField(field: FieldDeclaration) {
        context.currentClassWriter.visitField(
            modifiers = field.modifiers,
            name = field.name,
            type = field.type,
            defaultValue = field.defaultValue
        ).visitEnd()

        VertexASMLog.log(log, "${field.modifiers.joinToString(separator = " ", prefix = "", postfix = "") { it.name.lowercase() }} " +
            "${field.name} ${field.type.getDescriptor()} = ${field.defaultValue}")
    }
}