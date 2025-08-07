package com.lovelycatv.vertex.asm.lang

import com.lovelycatv.vertex.asm.lang.code.CodeWriter
import com.lovelycatv.vertex.asm.lang.code.IJavaCode

/**
 * @author lovelycat
 * @since 2025-08-02 17:19
 * @version 1.0
 */
abstract class CodeContainer {
    protected val codeWriter = CodeWriter(onCodeWritten = { this.addCode(it) })
    private val _code: MutableList<IJavaCode> = mutableListOf()

    fun getCodeList(): List<IJavaCode> = this._code

    fun writeCode(fx: CodeWriter.() -> Unit) {
        fx.invoke(this.codeWriter)
    }

    fun addCode(code: IJavaCode) {
        this._code.add(code)
    }
}