package com.pushtorefresh.storio.sqlitedb.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;

import java.util.concurrent.atomic.AtomicInteger;

abstract class OperationDesignTest {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @NonNull
    private final StorIOSQLiteDb storIOSQLiteDb = new DesignTestStorIOSQLiteDbImpl();

    @NonNull
    protected StorIOSQLiteDb storIOSQLiteDb() {
        return storIOSQLiteDb;
    }

    @NonNull
    protected User newUser() {
        return new User(null, "user" + COUNTER.getAndIncrement() + "@example.com");
    }
}
