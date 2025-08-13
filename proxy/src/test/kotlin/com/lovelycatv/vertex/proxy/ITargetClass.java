package com.lovelycatv.vertex.proxy;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-08-03 14:38
 */
public interface ITargetClass {
    int sayHello(String message);

    int add(int a, int b);

    int getIntArrayLength(int[] a);

    int complexFunction(String a, int[] b, float c, double[][] d, String[][] e);
}
