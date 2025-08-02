package com.lovelycatv.vertex.asm.loader

class ByteClassLoader internal constructor(
    private val className: String,
    private val classContent: ByteArray
) : ClassLoader() {
    private var classLoaded = false
    fun classLoaded(): Boolean {
        return classLoaded
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        return if (name == className) {
            classLoaded = true
            defineClass(className, classContent, 0, classContent.size)
        } else {
            super.loadClass(name, resolve)
        }
    }
}
