package com.pushtorefresh.storio.contentprovider.operation.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentProvider;
import com.pushtorefresh.storio.operation.PreparedOperation;

/**
 * Represents an Operation for {@link StorIOContentProvider} which performs insert or update data
 * in {@link android.content.ContentProvider}
 *
 * @param <T> type of data you want to put
 */
public abstract class PreparedPut<T, Result> implements PreparedOperation<Result> {

    @NonNull
    protected final StorIOContentProvider storIOContentProvider;

    @NonNull
    protected final PutResolver<T> putResolver;

    protected PreparedPut(@NonNull StorIOContentProvider storIOContentProvider, @NonNull PutResolver<T> putResolver) {
        this.storIOContentProvider = storIOContentProvider;
        this.putResolver = putResolver;
    }

    /**
     * Builder for {@link PreparedPut}
     */
    public static class Builder {

        @NonNull
        private final StorIOContentProvider storIOContentProvider;

        public Builder(@NonNull StorIOContentProvider storIOContentProvider) {
            this.storIOContentProvider = storIOContentProvider;
        }

        @NonNull
        public <T> PreparedPutObject.Builder<T> object(@NonNull T object) {
            return new PreparedPutObject.Builder<>(storIOContentProvider, object);
        }
    }
}
