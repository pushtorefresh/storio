package com.pushtorefresh.storio.contentresolver;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.operation.delete.PreparedDelete;
import com.pushtorefresh.storio.contentresolver.operation.get.PreparedGet;
import com.pushtorefresh.storio.contentresolver.operation.put.PreparedPut;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.query.InsertQuery;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.contentresolver.query.UpdateQuery;

import java.util.Collections;
import java.util.Set;

import rx.Observable;

/**
 * Powerful abstraction over {@link android.content.ContentResolver}
 */
public abstract class StorIOContentResolver {

    /**
     * Prepares "get" operation for {@link StorIOContentResolver}
     * Allows to get information from {@link StorIOContentResolver}
     *
     * @return builder for PreparedGet
     */
    @NonNull
    public PreparedGet.Builder get() {
        return new PreparedGet.Builder(this);
    }

    @NonNull
    public PreparedPut.Builder put() {
        return new PreparedPut.Builder(this);
    }

    @NonNull
    public PreparedDelete.Builder delete() {
        return new PreparedDelete.Builder(this);
    }

    /**
     * Subscribes to changes of required Uris
     *
     * @param uris set of {@link Uri} that should be monitored
     * @return {@link Observable} of {@link Changes} subscribed to changes of required Uris
     */
    @NonNull
    public abstract Observable<Changes> observeChangesOfUris(@NonNull Set<Uri> uris);

    /**
     * Subscribes to changes of required Uri
     *
     * @param uri {@link Uri} that should be monitored
     * @return {@link Observable} of {@link Changes} subscribed to changes of required Uri
     */
    @NonNull
    public Observable<Changes> observeChangesOfUri(@NonNull Uri uri) {
        return observeChangesOfUris(Collections.singleton(uri));
    }

    /**
     * Hides some internal operations of {@link StorIOContentResolver} to make API of {@link StorIOContentResolver} clean and easy to understand
     *
     * @return implementation of Internal operations for {@link StorIOContentResolver}
     */
    @NonNull
    public abstract Internal internal();

    /**
     * Hides some internal operations of {@link StorIOContentResolver}
     * to make {@link StorIOContentResolver} API clean and easy to understand
     */
    public static abstract class Internal {

        /**
         * Gets {@link ContentResolverTypeDefaults} for required type
         * <p>
         * Result can be null
         *
         * @param type type
         * @param <T>  type
         * @return {@link ContentResolverTypeDefaults} for required type or null
         */
        @Nullable
        public abstract <T> ContentResolverTypeDefaults<T> typeDefaults(@NonNull Class<T> type);

        /**
         * Gets the data from {@link StorIOContentResolver}
         *
         * @param query query
         * @return cursor with result data or null
         */
        @Nullable
        public abstract Cursor query(@NonNull Query query);

        /**
         * Inserts the data to {@link StorIOContentResolver}
         *
         * @param insertQuery   query
         * @param contentValues data
         * @return Uri for inserted data
         */
        @NonNull
        public abstract Uri insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues);

        /**
         * Updates data in {@link StorIOContentResolver}
         *
         * @param updateQuery   query
         * @param contentValues data
         * @return number of rows affected
         */
        public abstract int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues);

        /**
         * Deletes the data from {@link StorIOContentResolver}
         *
         * @param deleteQuery query
         * @return number of rows deleted
         */
        public abstract int delete(@NonNull DeleteQuery deleteQuery);
    }
}
