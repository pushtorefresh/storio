package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;

import org.junit.Test;

public class PutDesignTest {

    @NonNull BambooStorage getBambooStorage() {
        return new BambooStorageForDesignTest();
    }

    @Test public void putWasInserted() {
        boolean wasInserted = getBambooStorage()
                .forType(User.class)
                .put(new User())
                .wasUpdated();
    }

    @Test public void putWasUpdated() {
        boolean wasUpdated = getBambooStorage()
                .forType(User.class)
                .put(new User())
                .wasUpdated();
    }

    @Test public void getInsertedId() {
        Long insertedId = getBambooStorage()
                .forType(User.class)
                .put(new User())
                .getInsertedId();
    }

    @Test public void getUpdatedCount() {
        int updatedCount = getBambooStorage()
                .forType(User.class)
                .put(new User())
                .getUpdatedCount();
    }
}
