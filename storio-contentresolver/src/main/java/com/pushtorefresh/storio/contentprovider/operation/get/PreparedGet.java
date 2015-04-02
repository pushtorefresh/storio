package com.pushtorefresh.storio.contentprovider.operation.get;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentResolver;
import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;

/**
 * Represents an Operation for {@link StorIOContentResolver} which performs query that retrieves data
 * from {@link android.content.ContentProvider}
 *
 * @param <T> type of result
 */
public abstract class PreparedGet<T> implements PreparedOperationWithReactiveStream<T> {

    @NonNull
    protected final StorIOContentResolver storIOContentProvider;

    @NonNull
    protected final GetResolver getResolver;

    PreparedGet(@NonNull StorIOContentResolver storIOContentProvider, @NonNull GetResolver getResolver) {
        this.storIOContentProvider = storIOContentProvider;
        this.getResolver = getResolver;
    }

    /**
     * Builder for {@link PreparedGet}
     */
    public static class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentProvider;

        public Builder(@NonNull StorIOContentResolver storIOContentProvider) {
            this.storIOContentProvider = storIOContentProvider;
        }

        /**
         * Returns builder for {@link PreparedGetCursor}
         *
         * @return builder
         */
        @NonNull
        public PreparedGetCursor.Builder cursor() {
            return new PreparedGetCursor.Builder(storIOContentProvider);
        }

        /**
         * Returns builder for {@link PreparedGetListOfObjects}
         *
         * @return builder
         */
        @NonNull
        public <T> PreparedGetListOfObjects.Builder<T> listOfObjects(@NonNull Class<T> type) {
            return new PreparedGetListOfObjects.Builder<>(storIOContentProvider, type);
        }
    }
}
