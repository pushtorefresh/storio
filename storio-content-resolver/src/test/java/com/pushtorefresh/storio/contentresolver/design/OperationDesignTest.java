package com.pushtorefresh.storio.contentresolver.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;

abstract class OperationDesignTest {

    @NonNull
    private final StorIOContentResolver storIOContentResolver = new DesignTestStorIOContentResolver();

    @NonNull
    protected StorIOContentResolver storIOContentResolver() {
        return storIOContentResolver;
    }
}
