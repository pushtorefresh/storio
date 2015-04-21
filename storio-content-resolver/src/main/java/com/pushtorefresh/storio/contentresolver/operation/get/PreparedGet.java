package com.pushtorefresh.storio.contentresolver.operation.get;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;

/**
 * Represents an Operation for {@link StorIOContentResolver} which performs query that retrieves data
 * from {@link android.content.ContentProvider}
 *
 * @param <Result> type of result
 */
public abstract class PreparedGet<T, Result> implements PreparedOperationWithReactiveStream<Result> {

    @NonNull
    protected final StorIOContentResolver storIOContentResolver;

    @NonNull
    protected final GetResolver<T> getResolver;

    PreparedGet(@NonNull StorIOContentResolver storIOContentResolver, @NonNull GetResolver<T> getResolver) {
        this.storIOContentResolver = storIOContentResolver;
        this.getResolver = getResolver;
    }

    /**
     * Builder for {@link PreparedGet}
     */
    public static class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver) {
            this.storIOContentResolver = storIOContentResolver;
        }

        /**
         * Returns builder for {@link PreparedGetCursor}
         *
         * @return builder
         */
        @NonNull
        public PreparedGetCursor.Builder cursor() {
            return new PreparedGetCursor.Builder(storIOContentResolver);
        }

        /**
         * Returns builder for {@link PreparedGetListOfObjects}
         *
         * @return builder
         */
        @NonNull
        public <T> PreparedGetListOfObjects.Builder<T> listOfObjects(@NonNull Class<T> type) {
            return new PreparedGetListOfObjects.Builder<T>(storIOContentResolver, type);
        }
    }
}
