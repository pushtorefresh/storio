package com.pushtorefresh.storio.db.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;

import java.util.concurrent.atomic.AtomicInteger;

abstract class OperationDesignTest {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @NonNull private final StorIODb storIODb = new DesignTestStorIODbImpl();

    @NonNull protected StorIODb storIODb() {
        return storIODb;
    }

    @NonNull protected User newUser() {
        return new User(null, "user" + COUNTER.getAndIncrement() + "@example.com");
    }
}
