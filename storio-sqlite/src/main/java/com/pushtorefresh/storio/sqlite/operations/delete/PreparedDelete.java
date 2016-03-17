package com.pushtorefresh.storio.sqlite.operations.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operations.PreparedWriteOperation;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

import java.util.Collection;

/**
 * Prepared Delete Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of object to delete.
 */
public abstract class PreparedDelete<T> implements PreparedWriteOperation<T> {

    @NonNull
    protected final StorIOSQLite storIOSQLite;

    PreparedDelete(@NonNull StorIOSQLite storIOSQLite) {
        this.storIOSQLite = storIOSQLite;
    }

    /**
     * Builder for {@link PreparedDelete}.
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        public Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * Prepares Delete Operation by {@link com.pushtorefresh.storio.sqlite.queries.DeleteQuery}.
         *
         * @param deleteQuery query that specifies which rows should be deleted.
         * @return builder.
         */
        @NonNull
        public PreparedDeleteByQuery.Builder byQuery(@NonNull DeleteQuery deleteQuery) {
            return new PreparedDeleteByQuery.Builder(storIOSQLite, deleteQuery);
        }

        /**
         * Prepares Delete Operation which should delete one object.
         *
         * @param object object to delete.
         * @param <T>    type of the object.
         * @return builder.
         */
        @NonNull
        public <T> PreparedDeleteObject.Builder<T> object(@NonNull T object) {
            return new PreparedDeleteObject.Builder<T>(storIOSQLite, object);
        }

        /**
         * Prepares Delete Operation which should delete multiple objects.
         *
         * @param objects objects to delete.
         * @param <T>     type of objects.
         * @return builder.
         */
        @NonNull
        public <T> PreparedDeleteCollectionOfObjects.Builder<T> objects(@NonNull Collection<T> objects) {
            return new PreparedDeleteCollectionOfObjects.Builder<T>(storIOSQLite, objects);
        }
    }
}
