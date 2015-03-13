package com.pushtorefresh.storio.db.unit_test.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class OperationDesignTest {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @NonNull private final StorIODb storIODb = new DesignTestStorIOImpl();

    @NonNull protected StorIODb storIODb() {
        return storIODb;
    }

    @NonNull protected User newUser() {
        return new User(null, "user" + COUNTER.getAndIncrement() + "@example.com");
    }
}
