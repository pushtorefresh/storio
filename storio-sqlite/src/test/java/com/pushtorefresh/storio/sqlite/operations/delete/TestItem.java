package com.pushtorefresh.storio.sqlite.operations.delete;

import android.support.annotation.NonNull;

class TestItem {

    static final String TABLE = "test_items";

    static final String NOTIFICATION_TAG = "test_tag";

    private TestItem() {

    }

    @NonNull
    static TestItem newInstance() {
        return new TestItem();
    }
}
