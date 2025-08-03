package com.lovelycatv.vertex.aspect;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-08-03 14:34
 */
public class TargetClass implements ITargetClass {
    public TargetClass() {}

    @Override
    public void sayHello(String message) {
        System.out.println("Hello, " + message + "!");
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int getIntArrayLength(int[] a) {
        return a.length;
    }

    @Override
    public int complexFunction(String a, int[] b, float c, double[][] d, String[][] e) {
        return (int) (a.length() + b.length + c + d.length + e.length);
    }
}
