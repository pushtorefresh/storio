package com.pushtorefresh.storio.contentresolver;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.contentresolver.operations.delete.PreparedDelete;
import com.pushtorefresh.storio.contentresolver.operations.get.PreparedGet;
import com.pushtorefresh.storio.contentresolver.operations.put.PreparedPut;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;

import java.util.Collections;
import java.util.Set;

import rx.Observable;
import rx.Scheduler;

/**
 * Powerful abstraction over {@link android.content.ContentResolver}.
 */
public abstract class StorIOContentResolver {

    /**
     * Prepares "Get" Operation for {@link StorIOContentResolver}.
     * Allows you get information from {@link android.content.ContentProvider}.
     *
     * @return builder for {@link PreparedGet}.
     */
    @NonNull
    public PreparedGet.Builder get() {
        return new PreparedGet.Builder(this);
    }

    /**
     * Prepares "Put" Operation for {@link StorIOContentResolver}.
     * Allows you insert or update information in {@link android.content.ContentProvider}.
     *
     * @return builder for {@link PreparedPut}.
     */
    @NonNull
    public PreparedPut.Builder put() {
        return new PreparedPut.Builder(this);
    }

    /**
     * Prepares "Delete" Operation for {@link StorIOContentResolver}.
     * Allows you delete information from {@link android.content.ContentProvider}.
     *
     * @return builder for {@link PreparedDelete}.
     */
    @NonNull
    public PreparedDelete.Builder delete() {
        return new PreparedDelete.Builder(this);
    }

    /**
     * Allows observe changes of required set of {@link Uri}.
     * <p/>
     * Notice, that returned {@link Observable} is "Hot Observable", it never ends, which means,
     * that you should manually unsubscribe from it to prevent memory leak.
     * Also, it can cause BackPressure problems.
     *
     * @param uris set of {@link Uri} that should be monitored.
     * @return {@link Observable} of {@link Changes} subscribed to changes of required Uris.
     */
    @NonNull
    public abstract Observable<Changes> observeChangesOfUris(@NonNull Set<Uri> uris);

    /**
     * Allows observe changes of required {@link Uri}.
     * <p/>
     * Notice, that returned {@link Observable} is "Hot Observable", it never ends, which means,
     * that you should manually unsubscribe from it to prevent memory leak.
     * Also, it can cause BackPressure problems.
     *
     * @param uri {@link Uri} that should be monitored.
     * @return {@link Observable} of {@link Changes} subscribed to changes of required Uri.
     */
    @NonNull
    public Observable<Changes> observeChangesOfUri(@NonNull Uri uri) {
        return observeChangesOfUris(Collections.singleton(uri));
    }

    /**
     * Provides a scheduler on which {@link rx.Observable} / {@link rx.Single}
     * or {@link rx.Completable} will be subscribed.
     * <p/>
     * @see com.pushtorefresh.storio.operations.PreparedOperation#asRxObservable()
     * @see com.pushtorefresh.storio.operations.PreparedOperation#asRxSingle()
     * @see com.pushtorefresh.storio.operations.PreparedWriteOperation#asRxCompletable()
     *
     * @return the scheduler or {@code null} if it isn't needed to apply it.
     */
    @Nullable
    public abstract Scheduler defaultScheduler();

    /**
     * An API for low level interaction with {@link ContentResolver}, it's part of public API, so feel free to use it,
     * but please read documentation carefully!
     *
     * @return implementation of low level APIs for {@link StorIOContentResolver}.
     * @deprecated please use {@link #lowLevel()}, this one will be removed in v2.0,
     * basically, we just renamed it to LowLevel.
     */
    @Deprecated
    @NonNull
    public abstract Internal internal();

    /**
     * An API for low level interaction with {@link ContentResolver}, it's part of public API, so feel free to use it,
     * but please read documentation carefully!
     *
     * @return implementation of low level APIs for {@link StorIOContentResolver}.
     */
    @NonNull
    public abstract LowLevel lowLevel();

    /**
     * API for low level operations with {@link StorIOContentResolver}, we made it separate
     * to make {@link StorIOContentResolver} API clean and easy to understand.
     */
    public static abstract class LowLevel {

        /**
         * Gets {@link ContentResolverTypeMapping} for required type.
         * <p/>
         * Result can be {@code null}.
         *
         * @param type type.
         * @param <T>  type.
         * @return {@link ContentResolverTypeMapping} for required type or {@code null}.
         */
        @Nullable
        public abstract <T> ContentResolverTypeMapping<T> typeMapping(@NonNull Class<T> type);

        /**
         * Gets the data from {@link StorIOContentResolver}.
         *
         * @param query query.
         * @return cursor with result data or null.
         */
        @WorkerThread
        @NonNull
        public abstract Cursor query(@NonNull Query query);

        /**
         * Inserts the data to {@link StorIOContentResolver}.
         *
         * @param insertQuery   query.
         * @param contentValues data.
         * @return Uri for inserted data.
         */
        @WorkerThread
        @NonNull
        public abstract Uri insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues);

        /**
         * Updates data in {@link StorIOContentResolver}.
         *
         * @param updateQuery   query.
         * @param contentValues data.
         * @return number of rows affected.
         */
        @WorkerThread
        public abstract int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues);

        /**
         * Deletes the data from {@link StorIOContentResolver}.
         *
         * @param deleteQuery query.
         * @return number of rows deleted.
         */
        @WorkerThread
        public abstract int delete(@NonNull DeleteQuery deleteQuery);

        /**
         * Returns {@link ContentResolver} that can be used for operations
         * like {@link ContentResolver#applyBatch(String, java.util.ArrayList)} and so on!
         *
         * @return {@link ContentResolver}.
         */
        @NonNull
        public abstract ContentResolver contentResolver();
    }

    /**
     * @deprecated please use {@link LowLevel} instead, this type will be removed in v2.0,
     * basically we're just giving this API a better name.
     */
    @Deprecated
    public static abstract class Internal extends LowLevel {

    }
}
