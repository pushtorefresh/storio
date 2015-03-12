package com.pushtorefresh.android.bamboostorage.db.unit_test.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.BambooStorageDb;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class OperationDesignTest {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @NonNull private final BambooStorageDb bambooStorageDb = new DesignTestBambooStorageImpl();

    @NonNull protected BambooStorageDb bambooStorageDb() {
        return bambooStorageDb;
    }

    @NonNull protected User newUser() {
        return new User(null, "user" + COUNTER.getAndIncrement() + "@example.com");
    }
}
