package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;

public abstract class OperationDesignTest {

    @NonNull private final BambooStorage bambooStorage = new DesignTestBambooStorageImpl();

    @NonNull protected BambooStorage bambooStorage() {
        return bambooStorage;
    }
}
