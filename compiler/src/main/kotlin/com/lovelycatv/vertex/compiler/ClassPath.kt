package com.lovelycatv.vertex.compiler

object ClassPath {
    fun getUserDir(): String = System.getProperty("user.dir")

    object Maven {
        const val targetClasses = "/target/classes"
        const val targetTestClasses = "/target/test-classes"
        const val targetGeneratedSources = "/target/generated-sources"
        const val targetGeneratedTestSources = "/target/generated-test-sources"
    }
}