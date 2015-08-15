package com.pushtorefresh.storio.sqlite.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.internal.ChangesBus;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.RX_JAVA_IS_IN_THE_CLASS_PATH;
import static com.pushtorefresh.storio.internal.InternalQueries.nullableArrayOfStrings;
import static com.pushtorefresh.storio.internal.InternalQueries.nullableString;
import static java.util.Collections.unmodifiableMap;

/**
 * Default implementation of {@link StorIOSQLite} for {@link android.database.sqlite.SQLiteDatabase}.
 * <p>
 * Thread-safe.
 */
public class DefaultStorIOSQLite extends StorIOSQLite {

    @NonNull
    private final SQLiteOpenHelper sqLiteOpenHelper;

    @NonNull
    private final ChangesBus<Changes> changesBus = new ChangesBus<Changes>(RX_JAVA_IS_IN_THE_CLASS_PATH);

    /**
     * Implementation of {@link StorIOSQLite.Internal}.
     */
    @NonNull
    private final Internal internal;

    protected DefaultStorIOSQLite(@NonNull SQLiteOpenHelper sqLiteOpenHelper, @Nullable Map<Class<?>, SQLiteTypeMapping<?>> typesMapping) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        internal = new InternalImpl(typesMapping);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public Observable<Changes> observeChangesInTables(@NonNull final Set<String> tables) {
        final Observable<Changes> rxBus = changesBus.asObservable();

        if (rxBus == null) {
            throw new IllegalStateException("Observing changes in StorIOSQLite requires RxJava");
        }

        // indirect usage of RxJava filter() required to avoid problems with ClassLoader when RxJava is not in ClassPath
        return ChangesFilter.apply(rxBus, tables);
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
     * Closes underlying {@link SQLiteOpenHelper}.
     * <p>
     * All calls to this instance of {@link StorIOSQLite}
     * after call to this method can produce exceptions
     * and undefined behavior.
     */
    @Override
    public void close() throws IOException {
        sqLiteOpenHelper.close();
    }

    /**
     * Creates new builder for {@link DefaultStorIOSQLite}.
     *
     * @return non-null instance of {@link DefaultStorIOSQLite.Builder}.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link DefaultStorIOSQLite}.
     */
    public static final class Builder {

        /**
         * Please use {@link DefaultStorIOSQLite#builder()} instead of this.
         */
        Builder() {
        }

        /**
         * Required: Specifies SQLite Open helper for internal usage.
         * <p>
         *
         * @param sqliteOpenHelper a SQLiteOpenHelper for internal usage.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder sqliteOpenHelper(@NonNull SQLiteOpenHelper sqliteOpenHelper) {
            checkNotNull(sqliteOpenHelper, "Please specify SQLiteOpenHelper instance");
            return new CompleteBuilder(sqliteOpenHelper);
        }
    }

    /**
     * Compile-time safe part of builder for {@link DefaultStorIOSQLite}.
     */
    public static final class CompleteBuilder {

        @NonNull
        private final SQLiteOpenHelper sqLiteOpenHelper;

        private Map<Class<?>, SQLiteTypeMapping<?>> typesMapping;

        CompleteBuilder(@NonNull SQLiteOpenHelper sqLiteOpenHelper) {
            this.sqLiteOpenHelper = sqLiteOpenHelper;
        }

        /**
         * Adds {@link SQLiteTypeMapping} for some type.
         *
         * @param type        type.
         * @param typeMapping mapping for type.
         * @param <T>         type.
         * @return builder.
         */
        @NonNull
        public <T> CompleteBuilder addTypeMapping(@NonNull Class<T> type, @NonNull SQLiteTypeMapping<T> typeMapping) {
            checkNotNull(type, "Please specify type");
            checkNotNull(typeMapping, "Please specify type mapping");

            if (typesMapping == null) {
                typesMapping = new HashMap<Class<?>, SQLiteTypeMapping<?>>();
            }

            typesMapping.put(type, typeMapping);

            return this;
        }

        /**
         * Builds {@link DefaultStorIOSQLite} instance with required params.
         *
         * @return new {@link DefaultStorIOSQLite} instance.
         */
        @NonNull
        public DefaultStorIOSQLite build() {
            return new DefaultStorIOSQLite(sqLiteOpenHelper, typesMapping);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected class InternalImpl extends Internal {

        @NonNull
        private final Object lock = new Object();

        // Unmodifiable
        @Nullable
        private final Map<Class<?>, SQLiteTypeMapping<?>> directTypesMapping;

        @NonNull
        private final Map<Class<?>, SQLiteTypeMapping<?>> indirectTypesMappingCache
                = new ConcurrentHashMap<Class<?>, SQLiteTypeMapping<?>>();

        @NonNull
        private AtomicInteger numberOfRunningTransactions = new AtomicInteger(0);

        /**
         * Guarded by {@link #lock}.
         */
        @NonNull
        private Set<Changes> pendingChanges = new HashSet<Changes>(5);

        protected InternalImpl(@Nullable Map<Class<?>, SQLiteTypeMapping<?>> typesMapping) {
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
        public <T> SQLiteTypeMapping<T> typeMapping(final @NonNull Class<T> type) {
            if (directTypesMapping == null) {
                return null;
            }

            final SQLiteTypeMapping<T> directTypeMapping = (SQLiteTypeMapping<T>) directTypesMapping.get(type);

            if (directTypeMapping != null) {
                // fffast! O(1)
                return directTypeMapping;
            } else {
                // If no direct type mapping found — search for indirect type mapping

                // May be value already in cache.
                SQLiteTypeMapping<T> indirectTypeMapping
                        = (SQLiteTypeMapping<T>) indirectTypesMappingCache.get(type);

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
                    indirectTypeMapping = (SQLiteTypeMapping<T>) directTypesMapping.get(parentType);

                    if (indirectTypeMapping != null) {
                        // Store this typeMapping as known to make resolving O(1) for the next time
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
        @Override
        public void executeSQL(@NonNull RawQuery rawQuery) {
            sqLiteOpenHelper
                    .getWritableDatabase()
                    .execSQL(
                            rawQuery.query(),
                            nullableArrayOfStrings(rawQuery.args())
                    );
        }

        /**
         * {@inheritDoc}
         */
        @WorkerThread
        @NonNull
        @Override
        public Cursor rawQuery(@NonNull RawQuery rawQuery) {
            return sqLiteOpenHelper
                    .getReadableDatabase()
                    .rawQuery(
                            rawQuery.query(),
                            nullableArrayOfStrings(rawQuery.args())
                    );
        }

        /**
         * {@inheritDoc}
         */
        @WorkerThread
        @NonNull
        @Override
        public Cursor query(@NonNull Query query) {
            return sqLiteOpenHelper
                    .getReadableDatabase().query(
                            query.distinct(),
                            query.table(),
                            nullableArrayOfStrings(query.columns()),
                            nullableString(query.where()),
                            nullableArrayOfStrings(query.whereArgs()),
                            nullableString(query.groupBy()),
                            nullableString(query.having()),
                            nullableString(query.orderBy()),
                            nullableString(query.limit())
                    );
        }

        /**
         * {@inheritDoc}
         */
        @WorkerThread
        @Override
        public long insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues) {
            return sqLiteOpenHelper
                    .getWritableDatabase()
                    .insertOrThrow(
                            insertQuery.table(),
                            insertQuery.nullColumnHack(),
                            contentValues
                    );
        }

        /**
         * {@inheritDoc}
         */
        @WorkerThread
        @Override
        public int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues) {
            return sqLiteOpenHelper
                    .getWritableDatabase()
                    .update(
                            updateQuery.table(),
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
            return sqLiteOpenHelper
                    .getWritableDatabase()
                    .delete(
                            deleteQuery.table(),
                            nullableString(deleteQuery.where()),
                            nullableArrayOfStrings(deleteQuery.whereArgs())
                    );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void notifyAboutChanges(@NonNull Changes changes) {
            // Fast path, no synchronization required
            if (numberOfRunningTransactions.get() == 0) {
                changesBus.onNext(changes);
            } else {
                synchronized (lock) {
                    pendingChanges.add(changes);
                }

                notifyAboutPendingChangesIfNotInTransaction();
            }
        }

        private void notifyAboutPendingChangesIfNotInTransaction() {
            final Set<Changes> changesToSend;

            if (numberOfRunningTransactions.get() == 0) {
                synchronized (lock) {
                    changesToSend = pendingChanges;
                    pendingChanges = new HashSet<Changes>(5);
                }
            } else {
                changesToSend = null;
            }

            if (changesToSend != null) {
                for (Changes changes : changesToSend) {
                    changesBus.onNext(changes);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void beginTransaction() {
            sqLiteOpenHelper
                    .getWritableDatabase()
                    .beginTransaction();

            numberOfRunningTransactions.incrementAndGet();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTransactionSuccessful() {
            sqLiteOpenHelper
                    .getWritableDatabase()
                    .setTransactionSuccessful();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void endTransaction() {
            sqLiteOpenHelper
                    .getWritableDatabase()
                    .endTransaction();

            numberOfRunningTransactions.decrementAndGet();
            notifyAboutPendingChangesIfNotInTransaction();
        }
    }
}
