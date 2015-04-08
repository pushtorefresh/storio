package com.pushtorefresh.storio.sqlite.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import java.util.concurrent.atomic.AtomicInteger;

abstract class OperationDesignTest {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @NonNull
    private final StorIOSQLite storIOSQLite = new DesignTestStorIOSQLite();

    @NonNull
    protected StorIOSQLite storIOSQLite() {
        return storIOSQLite;
    }

    @NonNull
    protected User newUser() {
        return new User(null, "user" + COUNTER.getAndIncrement() + "@example.com");
    }
}
