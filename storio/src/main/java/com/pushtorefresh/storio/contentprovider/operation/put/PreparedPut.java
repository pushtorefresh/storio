package com.pushtorefresh.storio.contentprovider.operation.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;
import com.pushtorefresh.storio.db.operation.PreparedOperation;

public abstract class PreparedPut<T> implements PreparedOperation<T> {

    public static class Builder {

        @NonNull private final StorIOContentProvider storIOContentProvider;

        public Builder(@NonNull StorIOContentProvider storIOContentProvider) {
            this.storIOContentProvider = storIOContentProvider;
        }
    }
}
