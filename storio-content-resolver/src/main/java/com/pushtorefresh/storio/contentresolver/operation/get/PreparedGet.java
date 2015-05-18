package com.pushtorefresh.storio.contentresolver.operation.get;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.PreparedOperation;

/**
 * Represents Get Operation for {@link StorIOContentResolver}.
 *
 * @param <Result> type of result.
 */
public abstract class PreparedGet<T, Result> implements PreparedOperation<Result> {

    @NonNull
    protected final StorIOContentResolver storIOContentResolver;

    @NonNull
    protected final GetResolver<T> getResolver;

    PreparedGet(@NonNull StorIOContentResolver storIOContentResolver, @NonNull GetResolver<T> getResolver) {
        this.storIOContentResolver = storIOContentResolver;
        this.getResolver = getResolver;
    }

    /**
     * Builder for {@link PreparedGet}.
     */
    public final static class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver) {
            this.storIOContentResolver = storIOContentResolver;
        }

        /**
         * Returns builder for Get Operation that returns result as {@link android.database.Cursor}.
         *
         * @return builder for Get Operation that returns result as {@link android.database.Cursor}.
         */
        @NonNull
        public PreparedGetCursor.Builder cursor() {
            return new PreparedGetCursor.Builder(storIOContentResolver);
        }

        /**
         * Returns builder for Get Operation that returns result as {@link java.util.List} of items.
         *
         * @param type type of items.
         * @param <T>  type of items.
         * @return builder for Get Operation that returns result as {@link java.util.List} of items.
         */
        @NonNull
        public <T> PreparedGetListOfObjects.Builder<T> listOfObjects(@NonNull Class<T> type) {
            return new PreparedGetListOfObjects.Builder<T>(storIOContentResolver, type);
        }
    }
}
