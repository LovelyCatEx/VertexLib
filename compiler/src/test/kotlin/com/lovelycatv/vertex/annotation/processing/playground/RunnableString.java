package com.lovelycatv.vertex.annotation.processing.playground;

/**
 * @author lovelycat
 * @version 1.0
 * @since 2025-06-04 23:05
 */
public class RunnableString implements Runnable, CharSequence {
    @Override
    public void run() {

    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return "";
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
