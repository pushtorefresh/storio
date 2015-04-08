package com.pushtorefresh.storio.sqlite.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLiteDb;

import java.util.concurrent.atomic.AtomicInteger;

abstract class OperationDesignTest {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @NonNull
    private final StorIOSQLiteDb storIOSQLiteDb = new DesignTestStorIOSQLiteDb();

    @NonNull
    protected StorIOSQLiteDb storIOSQLiteDb() {
        return storIOSQLiteDb;
    }

    @NonNull
    protected User newUser() {
        return new User(null, "user" + COUNTER.getAndIncrement() + "@example.com");
    }
}
