package com.lovelycatv.vertex.annotation.processing

import com.lovelycatv.vertex.compiler.ClassPath
import com.lovelycatv.vertex.compiler.JavaCompiler
import java.io.File
import java.util.*
import javax.tools.DiagnosticCollector
import javax.tools.JavaFileObject
import kotlin.test.assertTrue


/**
 * @author lovelycat
 * @since 2025-05-26 23:21
 * @version 1.0
 */
class SharedVertexCompilerModuleTest {
    companion object {
        lateinit var processor: TestJavaAnnotationProcessor

        @JvmStatic
        fun testAnnotationProcessor() {
            val classPath = ClassPath.getUserDir() + ClassPath.Maven.targetTestClasses
            val outputPath = ClassPath.getUserDir() + ClassPath.Maven.targetTestClasses

            val javaFiles =
                File("src/test/kotlin/com/lovelycatv/vertex/annotation/processing/playground").listFiles()?.flatMap {
                    if (it.isDirectory) {
                        it.listFiles()?.toList() ?: emptyList()
                    } else {
                        listOf(it)
                    }
                } ?: emptyList()

            println(javaFiles.map { it.canonicalPath })

            this.processor = TestJavaAnnotationProcessor()

            val diagnosticCollector = DiagnosticCollector<JavaFileObject>()

            val task = JavaCompiler.compilationTaskBuilder {
                useSystemWriter()
                diagnosticCollector(diagnosticCollector)
                classPath(classPath)
                outputDir(outputPath)
                addCompilationUnits(javaFiles)
                fileManager(JavaCompiler.getJavaFileManager())
                addAnnotationProcessor(this@Companion.processor)
                setOption("source", "1.8")
                setLocale(Locale.SIMPLIFIED_CHINESE)
            }

            val success: Boolean = task.sourceVersion("1.8").build().call()

            if (!success) {
                println("Compilation failed, reasons:")
                diagnosticCollector.diagnostics.forEach {
                    println(it.getMessage(Locale.SIMPLIFIED_CHINESE))
                }
            }

            assertTrue(success, "Compilation failed")
        }
    }
}