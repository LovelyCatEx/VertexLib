package com.lovelycatv.vertex.annotation.processing.playground;

import com.lovelycatv.vertex.annotation.processing.annotations.TestAnnotation;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-05-27 15:07
 */
@TestAnnotation(name = "EverythingIncludedJavaClass")
public abstract class EverythingIncludedJavaClass<K extends CharSequence, V> extends EmptyJavaClass<K> {
    private static final String staticFinalString = "Hello, World!";
    public int primitiveInt = 999;
    private String privateString;
    protected String[] protectedStringArray;
    public volatile EmptyJavaClass<K> publicVolatileEmptyJavaClass;

    public String getPrivateString() {
        return privateString;
    }

    public String[] getProtectedStringArray() {
        return protectedStringArray;
    }

    public void setPrivateString(String privateString) {
        this.privateString = privateString;
    }

    public void setProtectedStringArray(String[] protectedStringArray) {
        this.protectedStringArray = protectedStringArray;
    }

    public EverythingIncludedJavaClass() {}

    public EverythingIncludedJavaClass(K k, V v) {}

    public void noParameterFunctionReturnsVoid() {}

    public Integer noParameterFunctionReturnsInteger() {
        return 0;
    }

    public void oneParameterFunctionReturnsVoid(Integer parameter) {}

    public Integer oneParameterFunctionReturnsInteger(Integer parameter) {
        return parameter;
    }

    public <FUNC_K, FUNC_V> String typeParameterizedFunction(FUNC_K k, FUNC_V v, K k1, V v1, Class<? super CharSequence> clazz) {
        return k.getClass().getCanonicalName();
    }

    protected abstract Integer[] abstractFunctionReturnsIntegerArray();
}
