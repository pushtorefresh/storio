package com.pushtorefresh.storio.sqlite.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.TypeMappingFinder;
import com.pushtorefresh.storio.internal.ChangesBus;
import com.pushtorefresh.storio.internal.TypeMappingFinderImpl;
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
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.RX_JAVA_IS_IN_THE_CLASS_PATH;
import static com.pushtorefresh.storio.internal.InternalQueries.nullableArrayOfStrings;
import static com.pushtorefresh.storio.internal.InternalQueries.nullableArrayOfStringsFromListOfStrings;
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

    @Nullable
    private final Scheduler defaultScheduler;

    /**
     * Implementation of {@link com.pushtorefresh.storio.sqlite.StorIOSQLite.LowLevel}.
     */
    @NonNull
    private final Internal lowLevel;

    protected DefaultStorIOSQLite(
            @NonNull SQLiteOpenHelper sqLiteOpenHelper,
            @NonNull TypeMappingFinder typeMappingFinder,
            @Nullable Scheduler defaultScheduler
    ) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        this.defaultScheduler = defaultScheduler;
        lowLevel = new LowLevelImpl(typeMappingFinder);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Observable<Changes> observeChanges() {
        final Observable<Changes> rxBus = changesBus.asObservable();

        if (rxBus == null) {
            throw new IllegalStateException("Observing changes in StorIOSQLite requires RxJava");
        }

        return rxBus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public Observable<Changes> observeChangesInTables(@NonNull final Set<String> tables) {
        // indirect usage of RxJava filter() required to avoid problems with ClassLoader when RxJava is not in ClassPath
        return ChangesFilter.applyForTables(observeChanges(), tables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public Observable<Changes> observeChangesOfTags(@NonNull final Set<String> tags) {
        // indirect usage of RxJava filter() required to avoid problems with ClassLoader when RxJava is not in ClassPath
        return ChangesFilter.applyForTags(observeChanges(), tags);
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
    @Deprecated
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

        private Map<Class<?>, SQLiteTypeMapping<?>> typeMapping;

        @Nullable
        private TypeMappingFinder typeMappingFinder;

        @Nullable
        private Scheduler defaultScheduler = RX_JAVA_IS_IN_THE_CLASS_PATH ? Schedulers.io() : null;

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

            if (this.typeMapping == null) {
                this.typeMapping = new HashMap<Class<?>, SQLiteTypeMapping<?>>();
            }

            this.typeMapping.put(type, typeMapping);

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
         * Builds {@link DefaultStorIOSQLite} instance with required params.
         *
         * @return new {@link DefaultStorIOSQLite} instance.
         */
        @NonNull
        public DefaultStorIOSQLite build() {

            if (typeMappingFinder == null) {
                typeMappingFinder = new TypeMappingFinderImpl();
            }
            if (typeMapping != null) {
                typeMappingFinder.directTypeMapping(unmodifiableMap(typeMapping));
            }

            return new DefaultStorIOSQLite(sqLiteOpenHelper, typeMappingFinder, defaultScheduler);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected class LowLevelImpl extends Internal {

        @NonNull
        private final Object lock = new Object();

        @NonNull
        private final TypeMappingFinder typeMappingFinder;

        @NonNull
        private AtomicInteger numberOfRunningTransactions = new AtomicInteger(0);

        /**
         * Guarded by {@link #lock}.
         */
        @NonNull
        private Set<Changes> pendingChanges = new HashSet<Changes>(5);

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
        public <T> SQLiteTypeMapping<T> typeMapping(final @NonNull Class<T> type) {
            return (SQLiteTypeMapping<T>) typeMappingFinder.findTypeMapping(type);
        }

        /**
         * {@inheritDoc}
         */
        @WorkerThread
        @Override
        public void executeSQL(@NonNull RawQuery rawQuery) {
            if (rawQuery.args().isEmpty()) {
                sqLiteOpenHelper
                        .getWritableDatabase()
                        .execSQL(rawQuery.query());
            } else {
                sqLiteOpenHelper
                        .getWritableDatabase()
                        .execSQL(
                                rawQuery.query(),
                                rawQuery.args().toArray(new Object[rawQuery.args().size()])
                        );
            }
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
                            nullableArrayOfStringsFromListOfStrings(query.columns()),
                            nullableString(query.where()),
                            nullableArrayOfStringsFromListOfStrings(query.whereArgs()),
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
        public long insertWithOnConflict(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues, int conflictAlgorithm) {
            return sqLiteOpenHelper
                    .getWritableDatabase()
                    .insertWithOnConflict(
                            insertQuery.table(),
                            insertQuery.nullColumnHack(),
                            contentValues,
                            conflictAlgorithm
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
                            nullableArrayOfStringsFromListOfStrings(updateQuery.whereArgs())
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
                            nullableArrayOfStringsFromListOfStrings(deleteQuery.whereArgs())
                    );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void notifyAboutChanges(@NonNull Changes changes) {
            checkNotNull(changes, "Changes can not be null");

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

            if (changesToSend != null && changesToSend.size() > 0) {
                final Set<String> affectedTables = new HashSet<String>(3);
                final Set<String> affectedTags = new HashSet<String>(3);
                for (Changes changes : changesToSend) {
                    // Merge all changes into one Changes object.
                    affectedTables.addAll(changes.affectedTables());
                    affectedTags.addAll(changes.affectedTags());
                }
                changesBus.onNext(Changes.newInstance(affectedTables, affectedTags));
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

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public SQLiteOpenHelper sqliteOpenHelper() {
            return sqLiteOpenHelper;
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
