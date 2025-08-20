package com.lovelycatv.vertex.asm.lang.code

/**
 * @author lovelycat
 * @since 2025-08-20 14:07
 * @version 1.0
 */
class DuplicateInstruction(val slotCount: Int = 1, val slotOffset: Int = 0) : IJavaCode {
    init {
        if (slotCount !in 1..2) {
            throw IllegalArgumentException("Slot count must be 1 or 2 only.")
        }

        if (slotOffset !in 0..2) {
            throw IllegalArgumentException("Slot offset must be 0, 1 or 2 only.")
        }
    }
}