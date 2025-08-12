package com.lovelycatv.vertex.reflect.loader

class ByteClassLoader internal constructor(
    private val className: String,
    private val classContent: ByteArray,
    parentLoader: ClassLoader = Thread.currentThread().contextClassLoader
) : ClassLoader(parentLoader) {
    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        return findLoadedClass(name)
            ?: if (name == className) {
                defineClass(className, classContent, 0, classContent.size)
            } else {
                super.loadClass(name, resolve)
            }
    }
}
