package com.lovelycatv.vertex.proxy.enhanced

import com.lovelycatv.vertex.log.logger
import net.sf.cglib.reflect.FastClass
import org.junit.jupiter.api.Test


/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-08-08 15:06
 */
class JavaEnhancedClassBenchmark {
    private val logger = logger()

    private fun timeAnalysis(preMsg: String, fx: () -> Unit) {
        val s = System.nanoTime()
        fx.invoke()
        logger.info("$preMsg: ${1.0 * (System.nanoTime() - s) / 1000000}ms")
    }

    private val largeClass = LargeClass()
    private val testTimes = 10000000

    @Test
    fun native() {
        timeAnalysis("Native       ") {
            for (i in 0..<testTimes) {
                largeClass.method1(1, 2.toByte(), 3f)
            }
        }
    }

    @Test
    fun nativeNew() {
        timeAnalysis("Native-New   ") {
            for (i in 0..<testTimes) {
                largeClass.method1(1, 2.toByte(), 3f)
            }
        }
    }

    @Test
    fun cglib() {
        System.setProperty("cglib.useCache", "false")
        val fastClass = FastClass.create(LargeClass::class.java)
        val method1Index = fastClass.getIndex("method1", arrayOf(Int::class.java, Byte::class.java, Float::class.java))
        timeAnalysis("Cglib        ") {
            for (i in 0..<testTimes) {
                fastClass.invoke(method1Index, largeClass, arrayOf(1, 2.toByte(), 3f))
            }
        }
    }

    @Test
    fun cglibMethodInvoke() {
        System.setProperty("cglib.useCache", "false")
        val fastClass = FastClass.create(LargeClass::class.java)
        val method1 = fastClass.getMethod("method1", arrayOf(Int::class.java, Byte::class.java, Float::class.java))
        timeAnalysis("Cglib-Method ") {
            for (i in 0..<testTimes) {
                method1.invoke(largeClass, arrayOf(1, 2.toByte(), 3f))
            }
        }
    }

    @Test
    fun cglibNew() {
        System.setProperty("cglib.useCache", "false")
        val fastClass = FastClass.create(LargeClass::class.java)
        val constructor = fastClass.getConstructor(arrayOf())
        timeAnalysis("Cglib-New    ") {
            for (i in 0..<testTimes) {
                constructor.newInstance(arrayOf())
            }
        }
    }

    @Test
    fun vertex() {
        val enhanced = EnhancedClass.createJava(LargeClass::class.java, true)
        val method1Index = enhanced.getIndex("method1", Int::class.java, Byte::class.java, Float::class.java)
        timeAnalysis("Vertex       ") {
            for (i in 0..<testTimes) {
                enhanced.invokeMethod(largeClass, method1Index, 1, 2.toByte(), 3f)
            }
        }
    }

    @Test
    fun vertexMethodInvoke() {
        val enhanced = EnhancedClass.createJava(LargeClass::class.java, true)
        val method1 = enhanced.getMethod("method1", Int::class.java, Byte::class.java, Float::class.java)
        timeAnalysis("Vertex-Method") {
            for (i in 0..<testTimes) {
                method1.invokeMethod(largeClass, 1, 2.toByte(), 3f)
            }
        }
    }

    @Test
    fun vertexNew() {
        val enhanced = EnhancedClass.createJava(LargeClass::class.java, true)
        val constructor = enhanced.getConstructor()
        timeAnalysis("Vertex-New    ") {
            for (i in 0..<testTimes) {
                constructor.invokeMethod()
            }
        }
    }
}
