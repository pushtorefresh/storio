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
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeDefaults;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.query.InsertQuery;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.contentresolver.query.UpdateQuery;
import com.pushtorefresh.storio.internal.ChangesBus;
import com.pushtorefresh.storio.internal.Queries;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.RX_JAVA_IS_AVAILABLE;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Default, thread-safe implementation of {@link StorIOContentResolver}.
 */
public class DefaultStorIOContentResolver extends StorIOContentResolver {

    @NonNull
    private final Internal internal;

    @NonNull
    private final ContentResolver contentResolver;

    @NonNull
    private final ChangesBus<Changes> changesBus = new ChangesBus<Changes>();

    // can be null, if RxJava is not available
    @Nullable
    private final ContentObserver contentObserver;

    protected DefaultStorIOContentResolver(@NonNull ContentResolver contentResolver, @Nullable Map<Class<?>, ContentResolverTypeDefaults<?>> typesDefaultsMap) {
        this.contentResolver = contentResolver;
        internal = new InternalImpl(typesDefaultsMap);

        if (RX_JAVA_IS_AVAILABLE) {
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
        throwExceptionIfRxJavaIsNotAvailable("Observing changes in StorIOContentProvider");

        for (Uri uri : uris) {
            contentResolver.registerContentObserver(
                    uri,
                    true,
                    contentObserver
            );
        }

        // indirect usage of RxJava filter() required to avoid problems with ClassLoader when RxJava is not in ClassPath
        return ChangesFilter.apply(changesBus.asObservable(), uris);
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
         * <p>
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

        private Map<Class<?>, ContentResolverTypeDefaults<?>> typesDefaultsMap;

        CompleteBuilder(@NonNull ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        /**
         * Adds {@link ContentResolverTypeDefaults} for some type.
         *
         * @param type         type.
         * @param typeDefaults defaults for type.
         * @param <T>          type.
         * @return builder.
         */
        @NonNull
        public <T> CompleteBuilder addDefaultsForType(@NonNull Class<T> type, ContentResolverTypeDefaults<T> typeDefaults) {
            checkNotNull(type, "Please specify type");
            checkNotNull(typeDefaults, "Please specify type defaults");

            if (typesDefaultsMap == null) {
                typesDefaultsMap = new HashMap<Class<?>, ContentResolverTypeDefaults<?>>();
            }

            if (typesDefaultsMap.containsKey(type)) {
                throw new IllegalArgumentException("Defaults for type " + type.getSimpleName() + " already added");
            }

            typesDefaultsMap.put(type, typeDefaults);

            return this;
        }

        /**
         * Builds new instance of {@link DefaultStorIOContentResolver}.
         *
         * @return new instance of {@link DefaultStorIOContentResolver}.
         */
        @NonNull
        public DefaultStorIOContentResolver build() {
            return new DefaultStorIOContentResolver(contentResolver, typesDefaultsMap);
        }
    }

    protected class InternalImpl extends Internal {

        @Nullable
        private final Map<Class<?>, ContentResolverTypeDefaults<?>> typesDefaultsMap;

        protected InternalImpl(@Nullable Map<Class<?>, ContentResolverTypeDefaults<?>> typesDefaultsMap) {
            this.typesDefaultsMap = typesDefaultsMap != null
                    ? Collections.unmodifiableMap(typesDefaultsMap)
                    : null;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> ContentResolverTypeDefaults<T> typeDefaults(@NonNull Class<T> type) {
            return typesDefaultsMap != null
                    ? (ContentResolverTypeDefaults<T>) typesDefaultsMap.get(type)
                    : null;
        }

        /**
         * {@inheritDoc}
         */
        @Nullable
        @Override
        public Cursor query(@NonNull Query query) {
            return contentResolver.query(
                    query.uri(),
                    Queries.listToArray(query.columns()),
                    query.where(),
                    Queries.listToArray(query.whereArgs()),
                    query.sortOrder()
            );
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
                    updateQuery.where(),
                    Queries.listToArray(updateQuery.whereArgs())
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int delete(@NonNull DeleteQuery deleteQuery) {
            return contentResolver.delete(
                    deleteQuery.uri(),
                    deleteQuery.where(),
                    Queries.listToArray(deleteQuery.whereArgs())
            );
        }
    }
}
