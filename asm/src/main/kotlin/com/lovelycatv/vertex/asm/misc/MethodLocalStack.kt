package com.lovelycatv.vertex.asm.misc

import com.lovelycatv.vertex.LinearBuffer
import com.lovelycatv.vertex.asm.lang.TypeDeclaration
import com.lovelycatv.vertex.stack.Stack

/**
 * @author lovelycat
 * @since 2025-08-02 12:32
 * @version 1.0
 */
class MethodLocalStack(private val stack: Stack<TypeDeclaration> = Stack()) : LinearBuffer<TypeDeclaration> by stack