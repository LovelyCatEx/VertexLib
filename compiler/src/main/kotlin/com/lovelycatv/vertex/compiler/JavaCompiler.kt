package com.lovelycatv.vertex.compiler

import java.nio.charset.Charset
import java.util.*
import javax.tools.Diagnostic
import javax.tools.JavaFileObject
import javax.tools.StandardJavaFileManager
import javax.tools.ToolProvider

object JavaCompiler {
    fun getInstance() = ToolProvider.getSystemJavaCompiler()

    fun getJavaFileManager(
        locale: Locale? = null,
        charset: Charset? = null,
        diagnosticListener: ((Diagnostic<out JavaFileObject>) -> Unit)? = null,
    ): StandardJavaFileManager {
        return getInstance().getStandardFileManager({ diagnosticListener?.invoke(it) }, locale, charset)
    }

    fun compilationTaskBuilder(): WrappedCompilationTask.Builder = WrappedCompilationTask.Builder(this.getInstance())

    fun compilationTaskBuilder(fx: WrappedCompilationTask.Builder.() -> Unit): WrappedCompilationTask.Builder {
        val builder = this.compilationTaskBuilder()
        fx.invoke(builder)
        return builder
    }
}