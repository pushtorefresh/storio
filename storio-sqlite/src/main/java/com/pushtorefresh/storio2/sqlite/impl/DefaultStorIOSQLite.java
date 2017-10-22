package com.pushtorefresh.storio2.sqlite.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio2.TypeMappingFinder;
import com.pushtorefresh.storio2.internal.ChangesBus;
import com.pushtorefresh.storio2.internal.TypeMappingFinderImpl;
import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.Interceptor;
import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio2.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio2.sqlite.queries.Query;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;
import com.pushtorefresh.storio2.sqlite.queries.UpdateQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import static com.pushtorefresh.storio2.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio2.internal.Environment.RX_JAVA_2_IS_IN_THE_CLASS_PATH;
import static com.pushtorefresh.storio2.internal.InternalQueries.nullableArrayOfStrings;
import static com.pushtorefresh.storio2.internal.InternalQueries.nullableArrayOfStringsFromListOfStrings;
import static com.pushtorefresh.storio2.internal.InternalQueries.nullableString;
import static com.pushtorefresh.storio2.internal.InternalQueries.unmodifiableNonNullList;
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
    private final ChangesBus<Changes> changesBus = new ChangesBus<Changes>(RX_JAVA_2_IS_IN_THE_CLASS_PATH);

    @Nullable
    private final Scheduler defaultRxScheduler;

    @NonNull
    private final List<Interceptor> interceptors;

    /**
     * Implementation of {@link com.pushtorefresh.storio2.sqlite.StorIOSQLite.LowLevel}.
     */
    @NonNull
    private final LowLevel lowLevel;

    protected DefaultStorIOSQLite(
            @NonNull SQLiteOpenHelper sqLiteOpenHelper,
            @NonNull TypeMappingFinder typeMappingFinder,
            @Nullable Scheduler defaultRxScheduler,
            @NonNull List<Interceptor> interceptors) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        this.defaultRxScheduler = defaultRxScheduler;
        this.interceptors = unmodifiableNonNullList(interceptors);
        lowLevel = new LowLevelImpl(typeMappingFinder);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Flowable<Changes> observeChanges(@NonNull BackpressureStrategy backpressureStrategy) {
        final Flowable<Changes> rxBus = changesBus.asFlowable();

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
    public Flowable<Changes> observeChangesInTables(@NonNull final Set<String> tables, @NonNull BackpressureStrategy backpressureStrategy) {
        // indirect usage of RxJava filter() required to avoid problems with ClassLoader when RxJava is not in ClassPath
        return ChangesFilter.applyForTables(observeChanges(backpressureStrategy), tables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public Flowable<Changes> observeChangesOfTags(@NonNull final Set<String> tags, @NonNull BackpressureStrategy backpressureStrategy) {
        // indirect usage of RxJava filter() required to avoid problems with ClassLoader when RxJava is not in ClassPath
        return ChangesFilter.applyForTags(observeChanges(backpressureStrategy), tags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Scheduler defaultRxScheduler() {
        return defaultRxScheduler;
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
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public List<Interceptor> interceptors() {
        return interceptors;
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
        private Scheduler defaultRxScheduler = RX_JAVA_2_IS_IN_THE_CLASS_PATH ? Schedulers.io() : null;

        @NonNull
        private List<Interceptor> interceptors = new ArrayList<Interceptor>();

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
         * Optional: Specifies a scheduler on which {@link Flowable} / {@link io.reactivex.Single}
         * or {@link Comparable} will be subscribed.
         * <p/>
         *
         * @return builder.
         * @see com.pushtorefresh.storio2.operations.PreparedOperation#asRxFlowable(BackpressureStrategy)
         * @see com.pushtorefresh.storio2.operations.PreparedOperation#asRxSingle()
         * @see com.pushtorefresh.storio2.operations.PreparedWriteOperation#asRxCompletable()
         */
        @NonNull
        public CompleteBuilder defaultRxScheduler(@Nullable Scheduler defaultRxScheduler) {
            this.defaultRxScheduler = defaultRxScheduler;
            return this;
        }

        /**
         * Optional: Adds {@link Interceptor} to all database operation.
         * Multiple interceptors would be called in the order they were added.
         *
         * @param interceptor non-null custom implementation of {@link Interceptor}.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder addInterceptor(@NonNull Interceptor interceptor) {
            interceptors.add(interceptor);
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

            return new DefaultStorIOSQLite(sqLiteOpenHelper, typeMappingFinder, defaultRxScheduler, interceptors);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected class LowLevelImpl extends LowLevel {

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
         * Executes a single SQL statement that
         * is NOT a SELECT/INSERT/UPDATE/DELETE on the database.
         * <p>
         * Notice: Direct call of this method will not trigger notification from {@link RawQuery#affectsTables()}.
         * To send it use {@link DefaultStorIOSQLite#executeSQL()} instead.
         *
         * @param rawQuery sql query.
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
         * Executes raw query on the database
         * and returns {@link android.database.Cursor} over the result set.
         * <p>
         * Notice: Direct call of this method will not trigger notification from {@link RawQuery#affectsTables()}.
         *
         * @param rawQuery sql query
         * @return A Cursor object, which is positioned before the first entry.
         * Note that Cursors are not synchronized, see the documentation for more details.
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
         * Inserts a row into the database.
         * <p>
         * Notice: Direct call of this method will not trigger notification,
         * you should do it manually with {@link #notifyAboutChanges(Changes)}
         * or use {@link DefaultStorIOSQLite#put()} instead.
         *
         * @param insertQuery   query.
         * @param contentValues map that contains the initial column values for the row.
         *                      The keys should be the column names and the values the column values.
         * @return id of inserted row.
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
         * Inserts a row into the database.
         * <p>
         * Notice: Direct call of this method will not trigger notification,
         * you should do it manually with {@link #notifyAboutChanges(Changes)}
         * or use {@link DefaultStorIOSQLite#put()} instead.
         *
         * @param insertQuery       query.
         * @param contentValues     map that contains the initial column values for the row.
         *                          The keys should be the column names and the values the column values.
         * @param conflictAlgorithm for insert conflict resolver.
         * @return the row ID of the newly inserted row OR the primary key of the existing row
         * if the input param 'conflictAlgorithm' = {@link android.database.sqlite.SQLiteDatabase#CONFLICT_IGNORE}
         * OR -1 if any error.
         * @see android.database.sqlite.SQLiteDatabase#insertWithOnConflict(String, String, ContentValues, int)
         * @see android.database.sqlite.SQLiteDatabase#CONFLICT_REPLACE
         * @see android.database.sqlite.SQLiteDatabase#CONFLICT_ABORT
         * @see android.database.sqlite.SQLiteDatabase#CONFLICT_FAIL
         * @see android.database.sqlite.SQLiteDatabase#CONFLICT_ROLLBACK
         * @see android.database.sqlite.SQLiteDatabase#CONFLICT_IGNORE
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
         * Updates one or multiple rows in the database.
         * <p>
         * Notice: Direct call of this method will not trigger notification,
         * you should do it manually with {@link #notifyAboutChanges(Changes)}
         * or use {@link DefaultStorIOSQLite#put()} instead.
         *
         * @param updateQuery   query.
         * @param contentValues a map from column names to new column values.
         *                      {@code null} is a valid value that will be translated to {@code NULL}.
         * @return the number of rows affected.
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
         * Deletes one or multiple rows in the database.
         * <p>
         * Notice: Direct call of this method will not trigger notification,
         * you should do it manually with {@link #notifyAboutChanges(Changes)}
         * or use {@link DefaultStorIOSQLite#delete()} instead.
         *
         * @param deleteQuery query.
         * @return the number of rows deleted.
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
}
