package com.lovelycatv.vertex.reflect;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-08-07 15:00
 */
public class BaseDataType {
    public static Class<Integer> PACKAGED_INTEGER_CLASS = java.lang.Integer.class;
    public static Class<Long> PACKAGED_LONG_CLASS = java.lang.Long.class;
    public static Class<Short> PACKAGED_SHORT_CLASS = java.lang.Short.class;
    public static Class<Float> PACKAGED_FLOAT_CLASS = java.lang.Float.class;
    public static Class<Double> PACKAGED_DOUBLE_CLASS = java.lang.Double.class;
    public static Class<Boolean> PACKAGED_BOOLEAN_CLASS = java.lang.Boolean.class;
    public static Class<Byte> PACKAGED_BYTE_CLASS = java.lang.Byte.class;
    public static Class<Character> PACKAGED_CHAR_CLASS = java.lang.Character.class;

    public static List<Class<?>> PACKAGED_PRIMITIVE_TYPE_CLASSES = Arrays.asList(
            PACKAGED_INTEGER_CLASS, PACKAGED_LONG_CLASS, PACKAGED_SHORT_CLASS, PACKAGED_FLOAT_CLASS,
            PACKAGED_DOUBLE_CLASS, PACKAGED_BOOLEAN_CLASS, PACKAGED_BYTE_CLASS, PACKAGED_CHAR_CLASS
    );

    public static String PACKAGED_INTEGER = PACKAGED_INTEGER_CLASS.getCanonicalName();
    public static String PACKAGED_LONG = PACKAGED_LONG_CLASS.getCanonicalName();
    public static String PACKAGED_SHORT = PACKAGED_SHORT_CLASS.getCanonicalName();
    public static String PACKAGED_FLOAT = PACKAGED_FLOAT_CLASS.getCanonicalName();
    public static String PACKAGED_DOUBLE = PACKAGED_DOUBLE_CLASS.getCanonicalName();
    public static String PACKAGED_BOOLEAN = PACKAGED_BOOLEAN_CLASS.getCanonicalName();
    public static String PACKAGED_BYTE = PACKAGED_BYTE_CLASS.getCanonicalName();
    public static String PACKAGED_CHAR = PACKAGED_CHAR_CLASS.getCanonicalName();

    public static List<String> PACKAGED_PRIMITIVE_TYPES = Arrays.asList(
        PACKAGED_INTEGER, PACKAGED_LONG, PACKAGED_SHORT, PACKAGED_FLOAT,
        PACKAGED_DOUBLE, PACKAGED_BOOLEAN, PACKAGED_BYTE, PACKAGED_CHAR
    );

    public static Class<Integer> INTEGER_CLASS = java.lang.Integer.TYPE;
    public static Class<?> LONG_CLASS = java.lang.Long.TYPE;
    public static Class<?> SHORT_CLASS = java.lang.Short.TYPE;
    public static Class<?> FLOAT_CLASS = java.lang.Float.TYPE;
    public static Class<?> DOUBLE_CLASS = java.lang.Double.TYPE;
    public static Class<?> BOOLEAN_CLASS = java.lang.Boolean.TYPE;
    public static Class<?> BYTE_CLASS = java.lang.Byte.TYPE;
    public static Class<?> CHAR_CLASS = java.lang.Character.TYPE;

    public static List<Class<?>> PRIMITIVE_TYPE_CLASSES = Arrays.asList(
            INTEGER_CLASS, LONG_CLASS, SHORT_CLASS, FLOAT_CLASS,
            DOUBLE_CLASS, BOOLEAN_CLASS, BYTE_CLASS, CHAR_CLASS
    );

    public static Class<?> getPrimitiveTypeClassByName(String name) {
        return PRIMITIVE_TYPE_CLASSES.stream()
                .filter(it -> it.getCanonicalName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not parse primitive type: " + name));
    }

    public static String INTEGER = "int";
    public static String LONG = "long";
    public static String SHORT = "short";
    public static String FLOAT = "float";
    public static String DOUBLE = "double";
    public static String BOOLEAN = "boolean";
    public static String BYTE = "byte";
    public static String CHAR = "char";

    public static List<String> PRIMITIVE_TYPES = Arrays.asList(INTEGER, LONG, SHORT, FLOAT, DOUBLE, BOOLEAN, BYTE, CHAR);

    /**
     * All classNames of base data types including primitive types
     */
    public static List<String> ALL = Stream.of(PACKAGED_PRIMITIVE_TYPES, PRIMITIVE_TYPES)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
}
