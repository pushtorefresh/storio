package com.pushtorefresh.storio.contentresolver.impl;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;
import static com.pushtorefresh.storio.internal.InternalQueries.nullableArrayOfStrings;
import static com.pushtorefresh.storio.internal.InternalQueries.nullableString;
import static java.util.Collections.unmodifiableMap;

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

    protected DefaultStorIOContentResolver(@NonNull ContentResolver contentResolver,
                                           @NonNull Handler contentObserverHandler,
                                           @Nullable Map<Class<?>, ContentResolverTypeMapping<?>> typesMapping) {
        this.contentResolver = contentResolver;
        this.contentObserverHandler = contentObserverHandler;
        internal = new InternalImpl(typesMapping);
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
        return RxChangesObserver.observeChanges(contentResolver, uris, contentObserverHandler, Build.VERSION.SDK_INT);
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
     * Creates new builder for {@link DefaultStorIOContentResolver}.
     *
     * @return not-null instance of {@link DefaultStorIOContentResolver.Builder}.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link DefaultStorIOContentResolver}.
     */
    public static final class Builder {

        /**
         * Please use {@link DefaultStorIOContentResolver#builder()} instead of this.
         */
        Builder() {
        }

        /**
         * Required: Specifies {@link ContentResolver} for {@link StorIOContentResolver}.
         * <p>
         * You can get in from any {@link android.content.Context}
         * instance: {@code context.getContentResolver().
         * It's safe to use {@link android.app.Activity} as {@link android.content.Context}.
         *
         * @param contentResolver not-null instance of {@link ContentResolver}.
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

        @Nullable
        private Map<Class<?>, ContentResolverTypeMapping<?>> typesMapping;

        @Nullable
        private Handler contentObserverHandler;

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
        public <T> CompleteBuilder addTypeMapping(@NonNull Class<T> type, @NonNull ContentResolverTypeMapping<T> typeMapping) {
            checkNotNull(type, "Please specify type");
            checkNotNull(typeMapping, "Please specify type mapping");

            if (typesMapping == null) {
                typesMapping = new HashMap<Class<?>, ContentResolverTypeMapping<?>>();
            }

            typesMapping.put(type, typeMapping);

            return this;
        }

        @NonNull
        public <T> CompleteBuilder contentObserverHandler(@NonNull Handler contentObserverHandler) {
            checkNotNull(contentObserverHandler, "contentObserverHandler should not be null");

            this.contentObserverHandler = contentObserverHandler;

            return this;
        }

        /**
         * Builds new instance of {@link DefaultStorIOContentResolver}.
         *
         * @return new instance of {@link DefaultStorIOContentResolver}.
         */
        @NonNull
        public DefaultStorIOContentResolver build() {
            if (contentObserverHandler == null) {
                final HandlerThread handlerThread = new HandlerThread("StorIOContentResolverNotificationsThread");
                handlerThread.start(); // multithreading: don't block me, bro!
                contentObserverHandler = new Handler(handlerThread.getLooper());
            }

            return new DefaultStorIOContentResolver(contentResolver, contentObserverHandler, typesMapping);
        }
    }

    protected class InternalImpl extends Internal {

        @Nullable
        private final Map<Class<?>, ContentResolverTypeMapping<?>> directTypesMapping;

        @NonNull
        private final Map<Class<?>, ContentResolverTypeMapping<?>> indirectTypesMappingCache
                = new ConcurrentHashMap<Class<?>, ContentResolverTypeMapping<?>>();

        protected InternalImpl(@Nullable Map<Class<?>, ContentResolverTypeMapping<?>> typesMapping) {
            this.directTypesMapping = typesMapping != null
                    ? unmodifiableMap(typesMapping)
                    : null;
        }

        /**
         * Gets type mapping for required type.
         * <p>
         * This implementation can handle subclasses of types, that registered its type mapping.
         * For example: You've added type mapping for {@code User.class},
         * and you have {@code UserFromServiceA.class} which extends {@code User.class},
         * and you didn't add type mapping for {@code UserFromServiceA.class}
         * because they have same fields and you just want to have multiple classes.
         * This implementation will find type mapping of {@code User.class}
         * and use it as type mapping for {@code UserFromServiceA.class}.
         *
         * @return direct or indirect type mapping for passed type, or {@code null}.
         */
        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> ContentResolverTypeMapping<T> typeMapping(@NonNull Class<T> type) {
            if (directTypesMapping == null) {
                return null;
            }

            final ContentResolverTypeMapping<T> directTypeMapping = (ContentResolverTypeMapping<T>) directTypesMapping.get(type);

            if (directTypeMapping != null) {
                // fffast! O(1)
                return directTypeMapping;
            } else {
                // If no direct type mapping found — search for indirect type mapping

                // May be value already in cache.
                ContentResolverTypeMapping<T> indirectTypeMapping =
                        (ContentResolverTypeMapping<T>) indirectTypesMappingCache.get(type);

                if (indirectTypeMapping != null) {
                    // fffast! O(1)
                    return indirectTypeMapping;
                }

                // Okay, we don't have direct type mapping.
                // And we don't have cache for indirect type mapping.
                // Let's find indirect type mapping and cache it!
                Class<?> parentType = type.getSuperclass();

                // Search algorithm:
                // Walk through all parent types of passed type.
                // If parent type has direct mapping -> we found indirect type mapping!
                // If current parent type == Object.class -> there is no indirect type mapping.
                // Complexity:
                // O(n) where n is number of parent types of passed type (pretty fast).

                // Stop search if root parent is Object.class
                while (parentType != Object.class) {
                    indirectTypeMapping = (ContentResolverTypeMapping<T>) directTypesMapping.get(parentType);

                    if (indirectTypeMapping != null) {
                        indirectTypesMappingCache.put(type, indirectTypeMapping);
                        return indirectTypeMapping;
                    }

                    parentType = parentType.getSuperclass();
                }

                // No indirect type mapping found.
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @WorkerThread
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

            if (cursor == null) {
                throw new IllegalStateException("Cursor returned by content provider is null");
            }

            return cursor;
        }

        /**
         * {@inheritDoc}
         */
        @WorkerThread
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
        @WorkerThread
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
        @WorkerThread
        @Override
        public int delete(@NonNull DeleteQuery deleteQuery) {
            return contentResolver.delete(
                    deleteQuery.uri(),
                    nullableString(deleteQuery.where()),
                    nullableArrayOfStrings(deleteQuery.whereArgs())
            );
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public ContentResolver contentResolver() {
            return contentResolver;
        }
    }
}
