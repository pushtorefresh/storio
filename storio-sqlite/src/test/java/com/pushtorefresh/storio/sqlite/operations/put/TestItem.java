package com.pushtorefresh.storio.sqlite.operations.put;

import android.support.annotation.NonNull;

class TestItem {
    static final String TABLE = "test_items";

    private TestItem() {

    }

    @NonNull
    static TestItem newInstance() {
        return new TestItem();
    }
}
