package com.lovelycatv.vertex.annotation.processing.playground;

import com.lovelycatv.vertex.annotation.processing.annotations.TestAnnotation;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-05-27 15:18
 */
@TestAnnotation(name = "EverythingIncludedJavaClassImpl", classArray = {EmptyJavaClass.class, Override.class}, clazz = Override.class)
public class EverythingIncludedJavaClassImpl extends EverythingIncludedJavaClass<String, String> {


    @Override
    protected Integer[] abstractFunctionReturnsIntegerArray() {
        return new Integer[0];
    }

    public void implClassFunction() {}
}
