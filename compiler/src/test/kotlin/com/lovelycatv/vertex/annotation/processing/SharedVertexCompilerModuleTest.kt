package com.lovelycatv.vertex.annotation.processing

import com.lovelycatv.vertex.compiler.ClassPath
import com.lovelycatv.vertex.compiler.JavaCompiler
import java.io.File
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

            this.processor = TestJavaAnnotationProcessor()

            val task = JavaCompiler.compilationTaskBuilder {
                useSystemWriter()
                fileManager(JavaCompiler.getJavaFileManager())
                classPath(classPath)
                outputDir(outputPath)
                addCompilationUnits(javaFiles)
                addAnnotationProcessor(this@Companion.processor)
            }

            val success: Boolean = task.sourceVersion("1.8").build().call()

            assertTrue(success, "Compilation failed")
        }
    }
}