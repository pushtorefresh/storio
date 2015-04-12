package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

class TestItem {

    private TestItem() {

    }

    @NonNull
    public static TestItem newInstance() {
        return new TestItem();
    }
}
