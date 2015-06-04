package com.pushtorefresh.storio.contentresolver.impl;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.query.InsertQuery;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.contentresolver.query.UpdateQuery;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;
import static com.pushtorefresh.storio.internal.Queries.nullableArrayOfStrings;
import static com.pushtorefresh.storio.internal.Queries.nullableString;

/**
 * Default, thread-safe implementation of {@link StorIOContentResolver}.
 */
public class DefaultStorIOContentResolver extends StorIOContentResolver {

    @NonNull
    private final Internal internal;

    @NonNull
    private final ContentResolver contentResolver;

    @NonNull
    private final Handler contentObserverHandler;

    protected DefaultStorIOContentResolver(@NonNull ContentResolver contentResolver, @Nullable Map<Class<?>, ContentResolverTypeMapping<?>> typesMapping) {
        this.contentResolver = contentResolver;
        internal = new InternalImpl(typesMapping);

        final HandlerThread handlerThread = new HandlerThread("StorIOContentResolverNotificationsThread");
        handlerThread.start(); // multithreading: don't block me, bro!

        contentObserverHandler = new Handler(handlerThread.getLooper());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public Observable<Changes> observeChangesOfUris(@NonNull final Set<Uri> uris) {
        throwExceptionIfRxJavaIsNotAvailable("Observing changes in StorIOContentProvider");

        // indirect usage of RxJava
        // required to avoid problems with ClassLoader when RxJava is not in ClassPath
        return RxChangesObserver.observeChanges(contentResolver, uris, contentObserverHandler);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Internal internal() {
        return internal;
    }

    /**
     * Builder for {@link DefaultStorIOContentResolver}.
     */
    public static final class Builder {

        /**
         * Required: Specifies {@link ContentResolver} for {@link StorIOContentResolver}.
         * <p/>
         * You can get in from any {@link android.content.Context}
         * instance: {@code context.getContentResolver().
         * It's safe to use {@link android.app.Activity} as {@link android.content.Context}.
         *
         * @param contentResolver non-null instance of {@link ContentResolver}.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder contentResolver(@NonNull ContentResolver contentResolver) {
            checkNotNull(contentResolver, "Please specify content resolver");
            return new CompleteBuilder(contentResolver);
        }
    }

    /**
     * Compile-time safe part of builder for {@link DefaultStorIOContentResolver}.
     */
    public static final class CompleteBuilder {

        @NonNull
        private final ContentResolver contentResolver;

        private Map<Class<?>, ContentResolverTypeMapping<?>> typesMapping;

        CompleteBuilder(@NonNull ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        /**
         * Adds {@link ContentResolverTypeMapping} for some type.
         *
         * @param type        type.
         * @param typeMapping mapping for type.
         * @param <T>         type.
         * @return builder.
         */
        @NonNull
        public <T> CompleteBuilder addTypeMapping(@NonNull Class<T> type, ContentResolverTypeMapping<T> typeMapping) {
            checkNotNull(type, "Please specify type");
            checkNotNull(typeMapping, "Please specify type mapping");

            if (typesMapping == null) {
                typesMapping = new HashMap<Class<?>, ContentResolverTypeMapping<?>>();
            }

            typesMapping.put(type, typeMapping);

            return this;
        }

        /**
         * Builds new instance of {@link DefaultStorIOContentResolver}.
         *
         * @return new instance of {@link DefaultStorIOContentResolver}.
         */
        @NonNull
        public DefaultStorIOContentResolver build() {
            return new DefaultStorIOContentResolver(contentResolver, typesMapping);
        }
    }

    protected class InternalImpl extends Internal {

        @Nullable
        private final Map<Class<?>, ContentResolverTypeMapping<?>> typesMapping;

        protected InternalImpl(@Nullable Map<Class<?>, ContentResolverTypeMapping<?>> typesMapping) {
            this.typesMapping = typesMapping != null
                    ? Collections.unmodifiableMap(typesMapping)
                    : null;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> ContentResolverTypeMapping<T> typeMapping(@NonNull Class<T> type) {
            return typesMapping != null
                    ? (ContentResolverTypeMapping<T>) typesMapping.get(type)
                    : null;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressLint("Recycle")
        @NonNull
        @Override
        public Cursor query(@NonNull Query query) {
            Cursor cursor = contentResolver.query(
                    query.uri(),
                    nullableArrayOfStrings(query.columns()),
                    nullableString(query.where()),
                    nullableArrayOfStrings(query.whereArgs()),
                    nullableString(query.sortOrder())
            );

            return cursor == null
                    ? new MatrixCursor(null, 0)
                    : cursor;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public Uri insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues) {
            return contentResolver.insert(
                    insertQuery.uri(),
                    contentValues
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues) {
            return contentResolver.update(
                    updateQuery.uri(),
                    contentValues,
                    nullableString(updateQuery.where()),
                    nullableArrayOfStrings(updateQuery.whereArgs())
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int delete(@NonNull DeleteQuery deleteQuery) {
            return contentResolver.delete(
                    deleteQuery.uri(),
                    nullableString(deleteQuery.where()),
                    nullableArrayOfStrings(deleteQuery.whereArgs())
            );
        }
    }
}
