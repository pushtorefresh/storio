package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.operation.PreparedOperation;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

import java.util.Collection;

public abstract class PreparedDelete<T> implements PreparedOperation<T> {

    @NonNull protected final StorIOSQLite storIOSQLiteDb;
    @NonNull protected final DeleteResolver deleteResolver;

    PreparedDelete(@NonNull StorIOSQLite storIOSQLiteDb, @NonNull DeleteResolver deleteResolver) {
        this.storIOSQLiteDb = storIOSQLiteDb;
        this.deleteResolver = deleteResolver;
    }

    /**
     * Builder for {@link PreparedDelete}
     */
    public static class Builder {

        @NonNull private final StorIOSQLite storIOSQLiteDb;

        public Builder(@NonNull StorIOSQLite storIOSQLiteDb) {
            this.storIOSQLiteDb = storIOSQLiteDb;
        }

        /**
         * Prepares Delete Operation by {@link com.pushtorefresh.storio.sqlite.query.DeleteQuery}
         *
         * @param deleteQuery query that specifies which rows should be deleted
         * @return builder
         */
        @NonNull public PreparedDeleteByQuery.Builder byQuery(@NonNull DeleteQuery deleteQuery) {
            return new PreparedDeleteByQuery.Builder(storIOSQLiteDb, deleteQuery);
        }

        /**
         * Prepares Delete Operation which should delete one object
         *
         * @param object object to delete
         * @param <T>    type of the object
         * @return builder
         */
        @NonNull public <T> PreparedDeleteObject.Builder<T> object(@NonNull T object) {
            return new PreparedDeleteObject.Builder<T>(storIOSQLiteDb, object);
        }

        /**
         * Prepares Delete Operation which should delete multiple objects
         *
         * @param objects objects to delete
         * @param <T>     type of objects
         * @return builder
         */
        @NonNull
        public <T> PreparedDeleteObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedDeleteObjects.Builder<T>(storIOSQLiteDb, objects);
        }
    }
}
