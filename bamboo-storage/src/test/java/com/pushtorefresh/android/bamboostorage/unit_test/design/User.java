package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;

public class User implements BambooStorableType {
    @Nullable @Override public String getStorableId() {
        return null;
    }
}
