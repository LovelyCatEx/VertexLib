package com.lovelycatv.vertex.asm.lang.code.calculate

import com.lovelycatv.vertex.asm.lang.TypeDeclaration

/**
 * @author lovelycat
 * @since 2025-08-07 12:17
 * @version 1.0
 */
class Calculation(
    val type: CalculateType,
    val numberType: TypeDeclaration
) : ICalculation