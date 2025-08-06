package com.lovelycatv.vertex.asm.processor

/**
 * @author lovelycat
 * @since 2025-08-06 17:49
 * @version 1.0
 */
abstract class AbstractDeclarationProcessor {
    protected abstract val processorContext: Context<*>

    abstract class Context<OWNER: AbstractDeclarationProcessor>(val owner: OWNER) {
        private var initialized = false

        /**
         * This function must be called before writing a new method.
         *
         */
        protected fun initialized() {
            this.initialized = true
        }

        /**
         * This function must be called after a new method written.
         *
         */
        protected fun consume() {
            if (this.initialized) {
                this.initialized = false
            } else {
                throw IllegalStateException("The processor has not been initialized.")
            }
        }
    }
}