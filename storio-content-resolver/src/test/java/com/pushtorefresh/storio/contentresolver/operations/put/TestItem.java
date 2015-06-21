package com.pushtorefresh.storio.contentresolver.operations.put;

import android.net.Uri;
import android.support.annotation.NonNull;

import static org.mockito.Mockito.mock;

class TestItem {
    static final Uri CONTENT_URI = mock(Uri.class);

    private TestItem() {

    }

    @NonNull
    static TestItem newInstance() {
        return new TestItem();
    }
}
