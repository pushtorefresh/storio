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

import com.pushtorefresh.storio.TypeMappingFinder;
import com.pushtorefresh.storio.internal.TypeMappingFinderImpl;
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

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.RX_JAVA_IS_IN_THE_CLASS_PATH;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;
import static com.pushtorefresh.storio.internal.InternalQueries.nullableArrayOfStringsFromListOfStrings;
import static com.pushtorefresh.storio.internal.InternalQueries.nullableString;
import static java.util.Collections.unmodifiableMap;

/**
 * Default, thread-safe implementation of {@link StorIOContentResolver}.
 */
public class DefaultStorIOContentResolver extends StorIOContentResolver {

    @NonNull
    private final Internal lowLevel;

    @NonNull
    private final ContentResolver contentResolver;

    @NonNull
    private final Handler contentObserverHandler;

    @Nullable
    private final Scheduler defaultScheduler;

    protected DefaultStorIOContentResolver(@NonNull ContentResolver contentResolver,
                                           @NonNull Handler contentObserverHandler,
                                           @NonNull TypeMappingFinder typeMappingFinder,
                                           @Nullable Scheduler defaultScheduler
    ) {
        this.contentResolver = contentResolver;
        this.contentObserverHandler = contentObserverHandler;
        this.defaultScheduler = defaultScheduler;
        lowLevel = new LowLevelImpl(typeMappingFinder);
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
    @Override
    public Scheduler defaultScheduler() {
        return defaultScheduler;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Internal internal() {
        return lowLevel;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public LowLevel lowLevel() {
        return lowLevel;
    }

    /**
     * Creates new builder for {@link DefaultStorIOContentResolver}.
     *
     * @return non-null instance of {@link DefaultStorIOContentResolver.Builder}.
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

        @Nullable
        private Map<Class<?>, ContentResolverTypeMapping<?>> typeMapping;

        @Nullable
        private Handler contentObserverHandler;

        @Nullable
        private TypeMappingFinder typeMappingFinder;

        private Scheduler defaultScheduler = RX_JAVA_IS_IN_THE_CLASS_PATH ? Schedulers.io() : null;

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

            if (this.typeMapping == null) {
                this.typeMapping = new HashMap<Class<?>, ContentResolverTypeMapping<?>>();
            }

            this.typeMapping.put(type, typeMapping);

            return this;
        }

        @NonNull
        public <T> CompleteBuilder contentObserverHandler(@NonNull Handler contentObserverHandler) {
            checkNotNull(contentObserverHandler, "contentObserverHandler should not be null");

            this.contentObserverHandler = contentObserverHandler;

            return this;
        }

        /**
         * Optional: Specifies {@link TypeMappingFinder} for low level usage.
         *
         * @param typeMappingFinder non-null custom implementation of {@link TypeMappingFinder}.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder typeMappingFinder(@NonNull TypeMappingFinder typeMappingFinder) {
            checkNotNull(typeMappingFinder, "Please specify typeMappingFinder");

            this.typeMappingFinder = typeMappingFinder;

            return this;
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
        @NonNull
        public CompleteBuilder defaultScheduler(@Nullable Scheduler defaultScheduler) {
            this.defaultScheduler = defaultScheduler;
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

            if (typeMappingFinder == null) {
                typeMappingFinder = new TypeMappingFinderImpl();
            }
            if (typeMapping != null) {
                typeMappingFinder.directTypeMapping(unmodifiableMap(typeMapping));
            }

            return new DefaultStorIOContentResolver(contentResolver, contentObserverHandler, typeMappingFinder, defaultScheduler);
        }
    }

    protected class LowLevelImpl extends Internal {

        @NonNull
        private final TypeMappingFinder typeMappingFinder;

        protected LowLevelImpl(@NonNull TypeMappingFinder typeMappingFinder) {
            this.typeMappingFinder = typeMappingFinder;
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
        @Nullable
        @Override
        public <T> ContentResolverTypeMapping<T> typeMapping(final @NonNull Class<T> type) {
            return (ContentResolverTypeMapping<T>) typeMappingFinder.findTypeMapping(type);
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
                    nullableArrayOfStringsFromListOfStrings(query.columns()),
                    nullableString(query.where()),
                    nullableArrayOfStringsFromListOfStrings(query.whereArgs()),
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
                    nullableArrayOfStringsFromListOfStrings(updateQuery.whereArgs())
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
                    nullableArrayOfStringsFromListOfStrings(deleteQuery.whereArgs())
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

    /**
     * Please use {@link LowLevelImpl} instead, this type will be remove in v2.0.
     */
    @Deprecated
    protected class InternalImpl extends LowLevelImpl {

        protected InternalImpl(@NonNull TypeMappingFinder typeMappingFinder) {
            super(typeMappingFinder);
        }
    }
}
