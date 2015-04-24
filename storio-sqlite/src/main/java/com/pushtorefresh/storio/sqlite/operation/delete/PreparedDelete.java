package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.PreparedOperation;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

public abstract class PreparedDelete<T> implements PreparedOperation<T> {

    @NonNull
    protected final StorIOSQLite storIOSQLite;

    PreparedDelete(@NonNull StorIOSQLite storIOSQLite) {
        this.storIOSQLite = storIOSQLite;
    }

    /**
     * Builder for {@link PreparedDelete}
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        public Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * Prepares Delete Operation by {@link com.pushtorefresh.storio.sqlite.query.DeleteQuery}
         *
         * @param deleteQuery query that specifies which rows should be deleted
         * @return builder
         */
        @NonNull
        public PreparedDeleteByQuery.Builder byQuery(@NonNull DeleteQuery deleteQuery) {
            return new PreparedDeleteByQuery.Builder(storIOSQLite, deleteQuery);
        }

        /**
         * Prepares Delete Operation which should delete one object
         *
         * @param object object to delete
         * @param <T>    type of the object
         * @return builder
         */
        @NonNull
        public <T> PreparedDeleteObject.Builder<T> object(@NonNull T object) {
            return new PreparedDeleteObject.Builder<T>(storIOSQLite, object);
        }

        /**
         * Prepares Delete Operation which should delete multiple objects
         *
         * @param type    type of objects, due to limitations of Generics in Java we have to explicitly ask you about type of objects, sorry :(
         * @param objects objects to delete
         * @param <T>     type of objects
         * @return builder
         */
        @NonNull
        public <T> PreparedDeleteObjects.Builder<T> objects(@NonNull Class<T> type, @NonNull Iterable<T> objects) {
            return new PreparedDeleteObjects.Builder<T>(storIOSQLite, type, objects);
        }
    }
}
