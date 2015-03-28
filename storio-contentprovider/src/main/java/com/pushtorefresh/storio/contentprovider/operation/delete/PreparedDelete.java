package com.pushtorefresh.storio.contentprovider.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;
import com.pushtorefresh.storio.operation.PreparedOperation;

public abstract class PreparedDelete<T> implements PreparedOperation<T> {

    public static class Builder {
        @NonNull private final StorIOContentProvider storIOContentProvider;

        public Builder(@NonNull StorIOContentProvider storIOContentProvider) {
            this.storIOContentProvider = storIOContentProvider;
        }
    }
}
