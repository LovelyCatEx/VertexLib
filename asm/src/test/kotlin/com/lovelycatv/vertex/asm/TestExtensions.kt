package com.lovelycatv.vertex.asm

import kotlin.test.assertContains

/**
 * @author lovelycat
 * @since 2025-08-07 16:19
 * @version 1.0
 */
class TestExtensions private constructor()

fun <E> assertContainsAll(array: Array<E>, vararg elements: E) {
    elements.forEach {
        assertContains(array, it)
    }
}
