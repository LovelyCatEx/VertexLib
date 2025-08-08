package com.lovelycatv.vertex.reflect.enhanced

import com.lovelycatv.vertex.log.logger
import com.lovelycatv.vertex.reflect.enhanced.factory.EnhancedClassByMethodHandleFactory
import com.lovelycatv.vertex.reflect.enhanced.factory.EnhancedClassByNativeFactory
import net.sf.cglib.reflect.FastClass
import org.junit.jupiter.api.Test


/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-08-08 15:06
 */
class EnhancedClassByNativeHandleBenchmark {
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
        timeAnalysis("Native") {
            for (i in 0..<testTimes) {
                largeClass.method1(1, 2.toByte(), 3f)
            }
        }
    }

    @Test
    fun cglib() {
        System.setProperty("cglib.useCache", "false")
        val fastClass = FastClass.create(LargeClass::class.java)
        val method1 = fastClass.getMethod("method1", arrayOf(Int::class.java, Byte::class.java, Float::class.java))
        timeAnalysis("Cglib") {
            for (i in 0..<testTimes) {
                method1.invoke(largeClass, arrayOf(1, 2.toByte(), 3f))
            }
        }
    }

    @Test
    fun vertex() {
        val enhanced = EnhancedClass.createByNative(LargeClass::class.java, true)
        val method1Index = enhanced.getIndex("method1", Int::class.java, Byte::class.java, Float::class.java)
        timeAnalysis("Vertex") {
            for (i in 0..<testTimes) {
                enhanced.invokeMethod(largeClass, method1Index, 1, 2.toByte(), 3f)
            }
        }
    }
}
