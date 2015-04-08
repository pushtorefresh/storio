package com.pushtorefresh.storio.contentresolver.impl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.query.InsertQuery;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.contentresolver.query.UpdateQuery;
import com.pushtorefresh.storio.util.QueryUtil;

import java.util.Set;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;
import static com.pushtorefresh.storio.util.EnvironmentUtil.IS_RX_JAVA_AVAILABLE;
import static com.pushtorefresh.storio.util.EnvironmentUtil.newRxJavaIsNotAvailableException;

/**
 * Default, thread-safe implementation of {@link StorIOContentResolver}
 */
public class DefaultStorIOContentResolver extends StorIOContentResolver {

    @NonNull
    private final Internal internal = new InternalImpl();

    @NonNull
    private final ContentResolver contentResolver;

    @Nullable
    private final PublishSubject<Changes> changesBus = IS_RX_JAVA_AVAILABLE
            ? PublishSubject.<Changes>create()
            : null;

    // can be null, if RxJava is not available
    @Nullable
    private final ContentObserver contentObserver;

    protected DefaultStorIOContentResolver(@NonNull ContentResolver contentResolver) {
        this.contentResolver = contentResolver;

        if (IS_RX_JAVA_AVAILABLE) {
            final HandlerThread handlerThread = new HandlerThread("StorIOContentResolverNotificationsThread");
            handlerThread.start(); // multithreading: don't block me, bro!

            contentObserver = new ContentObserver(new Handler(handlerThread.getLooper())) {
                @Override
                public boolean deliverSelfNotifications() {
                    return false;
                }

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    // sending changes to changesBus
                    changesBus.onNext(Changes.newInstance(uri));
                }
            };
        } else {
            contentObserver = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public Observable<Changes> observeChangesOfUris(@NonNull final Set<Uri> uris) {
        if (!IS_RX_JAVA_AVAILABLE) {
            throw newRxJavaIsNotAvailableException("Observing changes in StorIOContentProvider");
        }

        for (Uri uri : uris) {
            contentResolver.registerContentObserver(
                    uri,
                    true,
                    contentObserver
            );
        }

        // indirect usage of RxJava filter() required to avoid problems with ClassLoader when RxJava is not in ClassPath
        return ChangesFilter.apply(changesBus, uris);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Internal internal() {
        return internal;
    }

    protected class InternalImpl extends Internal {

        /**
         * {@inheritDoc}
         */
        @Nullable
        @Override
        public Cursor query(@NonNull Query query) {
            return contentResolver.query(
                    query.uri,
                    QueryUtil.listToArray(query.projection),
                    query.where,
                    QueryUtil.listToArray(query.whereArgs),
                    query.sortOrder
            );
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public Uri insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues) {
            return contentResolver.insert(
                    insertQuery.uri,
                    contentValues
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues) {
            return contentResolver.update(
                    updateQuery.uri,
                    contentValues,
                    updateQuery.where,
                    QueryUtil.listToArray(updateQuery.whereArgs)
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int delete(@NonNull DeleteQuery deleteQuery) {
            return contentResolver.delete(
                    deleteQuery.uri,
                    deleteQuery.where,
                    QueryUtil.listToArray(deleteQuery.whereArgs)
            );
        }
    }


    /**
     * Builder for {@link DefaultStorIOContentResolver}
     */
    public static class Builder {

        protected ContentResolver contentResolver;

        /**
         * Required: Specifies {@link ContentResolver} for {@link StorIOContentResolver}
         * <p>
         * You can get in from any {@link android.content.Context} instance: <code>context.getContentResolver()</code>
         * It's safe to use {@link android.app.Activity} as {@link android.content.Context}
         *
         * @param contentResolver non-null instance of {@link ContentResolver}
         * @return builder
         */
        @NonNull
        public CompleteBuilder contentResolver(@NonNull ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
            return new CompleteBuilder(this);
        }
    }

    /**
     * Compile-time safe part of builder for {@link DefaultStorIOContentResolver}
     */
    public static class CompleteBuilder extends Builder {

        CompleteBuilder(@NonNull Builder builder) {
            contentResolver = builder.contentResolver;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public CompleteBuilder contentResolver(@NonNull ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
            return this;
        }

        /**
         * Builds new instance of {@link DefaultStorIOContentResolver}
         *
         * @return new instance of {@link DefaultStorIOContentResolver}
         */
        @NonNull
        public DefaultStorIOContentResolver build() {
            checkNotNull(contentResolver, "Please specify content resolver");

            return new DefaultStorIOContentResolver(contentResolver);
        }
    }
}
