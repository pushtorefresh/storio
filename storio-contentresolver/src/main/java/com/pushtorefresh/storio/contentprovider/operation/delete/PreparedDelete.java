package com.pushtorefresh.storio.contentprovider.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentResolver;
import com.pushtorefresh.storio.operation.PreparedOperation;

public abstract class PreparedDelete<T> implements PreparedOperation<T> {

    public static class Builder {
        @NonNull private final StorIOContentResolver storIOContentProvider;

        public Builder(@NonNull StorIOContentResolver storIOContentProvider) {
            this.storIOContentProvider = storIOContentProvider;
        }
    }
}
