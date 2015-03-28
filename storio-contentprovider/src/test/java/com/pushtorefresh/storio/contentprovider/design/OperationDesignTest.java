package com.pushtorefresh.storio.contentprovider.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;

abstract class OperationDesignTest {

    @NonNull
    private final StorIOContentProvider storIOContentProvider = new DesignTestStorIOContentProviderImpl();

    @NonNull
    protected StorIOContentProvider storIOContentProvider() {
        return storIOContentProvider;
    }
}
