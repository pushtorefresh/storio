package com.pushtorefresh.android.bamboostorage.db.unit_test.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.BambooStorage;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class OperationDesignTest {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @NonNull private final BambooStorage bambooStorage = new DesignTestBambooStorageImpl();

    @NonNull protected BambooStorage bambooStorage() {
        return bambooStorage;
    }

    @NonNull protected User newUser() {
        return new User(null, "artem.zinnatullin" + COUNTER.getAndIncrement() + "@gmail.com");
    }
}
