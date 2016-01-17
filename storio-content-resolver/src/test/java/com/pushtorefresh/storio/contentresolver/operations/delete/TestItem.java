package com.pushtorefresh.storio.contentresolver.operations.delete;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class TestItem {

    @Nullable
    private final String data;

    private TestItem(@Nullable String data) {
        this.data = data;
    }

    @NonNull
    public static TestItem newInstance() {
        return new TestItem(null);
    }

    @NonNull
    public static TestItem newInstance(@Nullable String data) {
        return new TestItem(data);
    }

    @Override
    public String toString() {
        return "TestItem{" +
                "data='" + data + '\'' +
                '}';
    }
}
