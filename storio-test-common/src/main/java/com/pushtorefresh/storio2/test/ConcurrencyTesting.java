package com.pushtorefresh.storio2.test;

public class ConcurrencyTesting {

    private ConcurrencyTesting() {
        throw new IllegalStateException("No instances please.");
    }

    public static int optimalTestThreadsCount() {
        return Math.max(Runtime.getRuntime().availableProcessors() * 2, 4);
    }
}
