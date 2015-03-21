package com.pushtorefresh.storio.contentprovider.operation.get;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;
import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;

public abstract class PreparedGet<T> implements PreparedOperationWithReactiveStream<T> {

    public static class Builder {

        @NonNull private final StorIOContentProvider storIOContentProvider;

        public Builder(@NonNull StorIOContentProvider storIOContentProvider) {
            this.storIOContentProvider = storIOContentProvider;
        }
    }
}
