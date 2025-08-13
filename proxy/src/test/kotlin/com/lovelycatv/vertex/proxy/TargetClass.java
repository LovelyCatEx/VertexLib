package com.lovelycatv.vertex.proxy;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-08-03 14:34
 */
public class TargetClass implements ITargetClass {
    public TargetClass() {}

    public TargetClass(String a, int[] b) {}

    @Override
    public int sayHello(String message) {
        System.out.println("Hello, " + message + "!");
        return message.length();
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

    public void throwing() throws IllegalArgumentException {
        throw new IllegalArgumentException("Oops");
    }
}
