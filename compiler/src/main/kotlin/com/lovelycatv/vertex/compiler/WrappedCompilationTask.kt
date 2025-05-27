package com.lovelycatv.vertex.compiler

import java.io.File
import java.io.Writer
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Callable
import javax.annotation.processing.AbstractProcessor
import javax.tools.DiagnosticCollector
import javax.tools.JavaCompiler
import javax.tools.JavaFileObject
import javax.tools.StandardJavaFileManager

/**
 * Wrapped [javax.tools.JavaCompiler.CompilationTask]
 *
 * @property writer a Writer for additional output from the compiler; use [System.err] if null
 * @property fileManager a file manager; if null use the compiler's standard file manager
 * @property compilationUnits the compilation units to compile, null means no compilation units
 * @property diagnosticCollector a diagnostic listener; if null use the compiler's default method for reporting
 * @property options compiler options, null means no options
 * @property classes names of classes to be processed by annotation processing, null means no class names
 *
 * @author lovelycat
 * @since 2025-05-27 12:45
 * @version 1.0
 */
class WrappedCompilationTask(
    private val compiler: JavaCompiler,
    private val writer: Writer?,
    private val fileManager: StandardJavaFileManager,
    private val compilationUnits: MutableList<JavaFileObject>,
    private val diagnosticCollector: DiagnosticCollector<JavaFileObject>,
    private val options: MutableMap<String, List<String>>,
    private val classes: MutableList<String>,
    private val annotationProcessors: MutableList<AbstractProcessor>,
    private val modules: MutableList<String>,
    private val locale: Locale?
) : Callable<Boolean> {
    fun getTask(): JavaCompiler.CompilationTask? {
        return this.compiler.getTask(
            this.writer,
            this.fileManager,
            this.diagnosticCollector,
            this.options.flatMap { listOf(it.key) + it.value },
            this.classes,
            this.compilationUnits
        ).apply {
            setLocale(locale)
            setProcessors(annotationProcessors)
            addModules(modules)
        }
    }

    override fun call(): Boolean {
        return this.getTask()?.call() == true
    }

    class Builder(private val compiler: JavaCompiler) {
        private var writer: Writer? = null
        private var fileManager: StandardJavaFileManager = com.lovelycatv.vertex.compiler.JavaCompiler.getJavaFileManager {}
        private val compilationUnits: MutableList<JavaFileObject> = mutableListOf()
        private var diagnosticCollector: DiagnosticCollector<JavaFileObject> = DiagnosticCollector()
        private val options: MutableMap<String, List<String>> = mutableMapOf()
        private val classes: MutableList<String> = mutableListOf()
        private val annotationProcessors: MutableList<AbstractProcessor> = mutableListOf()
        private val modules: MutableList<String>  = mutableListOf()
        private var locale: Locale? = null

        fun setWriter(writer: Writer): Builder {
            this.writer = writer
            return this
        }

        fun useSystemWriter(charset: Charset = Charsets.UTF_8): Builder {
            return this.setWriter(System.out.writer(charset))
        }

        fun diagnosticCollector(collector: DiagnosticCollector<JavaFileObject>): Builder {
            this.diagnosticCollector = collector
            return this
        }

        fun fileManager(fileManager: StandardJavaFileManager): Builder {
            this.fileManager = fileManager
            return this
        }

        fun addCompilationUnits(vararg fileObject: JavaFileObject): Builder {
            this.compilationUnits.addAll(fileObject)
            return this
        }

        fun addCompilationUnits(vararg files: File): Builder {
            return this.addCompilationUnits(files.toList())
        }

        fun addCompilationUnits(files: Iterable<File>): Builder {
            this.compilationUnits.addAll(fileManager.getJavaFileObjectsFromFiles(files))
            return this
        }

        fun setOption(key: String, vararg values: String): Builder {
            val realKey = if (key.startsWith("-")) key else "-$key"
            this.options[realKey] = values.toList()
            return this
        }

        fun addClass(className: String): Builder {
            this.classes.add(className)
            return this
        }

        fun addAnnotationProcessor(processor: AbstractProcessor): Builder {
            this.annotationProcessors.add(processor)
            return this
        }

        fun addModule(moduleName: String): Builder {
            this.modules.add(moduleName)
            return this
        }

        fun setLocale(locale: Locale): Builder {
            this.locale = locale
            return this
        }

        fun classPath(classPath: String): Builder {
            this.setOption("-classpath", classPath)
            return this
        }

        fun outputDir(dir: String): Builder {
            this.setOption("-d", dir)
            return this
        }

        fun verbose(): Builder {
            this.setOption("-verbose")
            return this
        }

        fun sourceVersion(version: String): Builder {
            this.setOption("-source", version)
            return this
        }

        fun build(): WrappedCompilationTask {
            return WrappedCompilationTask(
                this.compiler,
                this.writer,
                this.fileManager,
                this.compilationUnits,
                this.diagnosticCollector,
                this.options,
                this.classes,
                this.annotationProcessors,
                this.modules,
                this.locale
            )
        }
    }
}