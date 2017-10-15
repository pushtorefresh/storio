package com.pushtorefresh.storio2.test;

public class ConcurrencyTesting {

    private ConcurrencyTesting() {
        throw new IllegalStateException("No instances!");
    }

    public static int optimalTestThreadsCount() {
        return Runtime.getRuntime().availableProcessors() * 2;
    }
}
