package com.lovelycatv.vertex.reflect

object BaseDataType {
    const val STRING_CLASS = "java.lang.String"
    const val INTEGER_CLASS = "java.lang.Integer"
    const val LONG_CLASS = "java.lang.Long"
    const val SHORT_CLASS = "java.lang.Short"
    const val FLOAT_CLASS = "java.lang.Float"
    const val DOUBLE_CLASS = "java.lang.Double"
    const val BOOLEAN_CLASS = "java.lang.Boolean"
    const val BYTE_CLASS = "java.lang.Byte"

    @JvmStatic
    val BASE_DATA_TYPES = listOf(
        STRING_CLASS, INTEGER_CLASS, LONG_CLASS, SHORT_CLASS,
        FLOAT_CLASS, DOUBLE_CLASS, BOOLEAN_CLASS, BYTE_CLASS,
    )

    const val STRING = "string"
    const val INTEGER = "int"
    const val LONG = "long"
    const val SHORT = "short"
    const val FLOAT = "float"
    const val DOUBLE = "double"
    const val BOOLEAN = "boolean"
    const val BYTE = "byte"

    @JvmStatic
    val PRIMITIVE_TYPES = listOf(STRING, INTEGER, LONG, SHORT, FLOAT, DOUBLE, BOOLEAN, BYTE)

    /**
     * All classNames of base data types including primitive types
     */
    @JvmStatic
    val ALL = BASE_DATA_TYPES + PRIMITIVE_TYPES
}