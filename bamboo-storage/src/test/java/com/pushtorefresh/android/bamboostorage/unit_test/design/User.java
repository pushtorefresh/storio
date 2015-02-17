package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;

import java.lang.Override;

public class User implements BambooStorableType {
    @Nullable @Override public Long getStorableId() {
        return null;
    }
    @Override public serStorableId(@Nullable Long storableId) { }
}
