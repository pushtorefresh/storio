package com.pushtorefresh.storio.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.operation.PreparedOperation;
import com.pushtorefresh.storio.db.query.DeleteQuery;

import java.util.Collection;

public abstract class PreparedDelete<T> implements PreparedOperation<T> {

    @NonNull protected final StorIODb storIODb;
    @NonNull protected final DeleteResolver deleteResolver;

    PreparedDelete(@NonNull StorIODb storIODb, @NonNull DeleteResolver deleteResolver) {
        this.storIODb = storIODb;
        this.deleteResolver = deleteResolver;
    }

    /**
     * Builder for {@link PreparedDelete}
     */
    public static class Builder {

        @NonNull private final StorIODb storIODb;

        public Builder(@NonNull StorIODb storIODb) {
            this.storIODb = storIODb;
        }

        /**
         * Prepares Delete Operation by {@link com.pushtorefresh.storio.db.query.DeleteQuery}
         *
         * @param deleteQuery query that specifies which rows should be deleted
         * @return builder
         */
        @NonNull public PreparedDeleteByQuery.Builder byQuery(@NonNull DeleteQuery deleteQuery) {
            return new PreparedDeleteByQuery.Builder(storIODb, deleteQuery);
        }

        /**
         * Prepares Delete Operation which should delete one object
         *
         * @param object object to delete
         * @param <T>    type of the object
         * @return builder
         */
        @NonNull public <T> PreparedDeleteObject.Builder<T> object(@NonNull T object) {
            return new PreparedDeleteObject.Builder<>(storIODb, object);
        }

        /**
         * Prepares Delete Operation which should delete multiple objects
         *
         * @param objects objects to delete
         * @param <T>     type of objects
         * @return builder
         */
        @NonNull
        public <T> PreparedDeleteCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedDeleteCollectionOfObjects.Builder<>(storIODb, objects);
        }
    }
}
