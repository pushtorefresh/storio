package com.pushtorefresh.storio.contentprovider.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentResolver;

abstract class OperationDesignTest {

    @NonNull
    private final StorIOContentResolver storIOContentProvider = new DesignTestStorIOContentProviderImpl();

    @NonNull
    protected StorIOContentResolver storIOContentProvider() {
        return storIOContentProvider;
    }
}
