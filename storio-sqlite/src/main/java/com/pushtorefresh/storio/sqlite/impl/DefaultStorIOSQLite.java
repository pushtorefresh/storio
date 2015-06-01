package com.pushtorefresh.storio.sqlite.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.internal.ChangesBus;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;
import com.pushtorefresh.storio.sqlite.query.InsertQuery;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;
import com.pushtorefresh.storio.sqlite.query.UpdateQuery;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Queries.nullableArrayOfStrings;
import static com.pushtorefresh.storio.internal.Queries.nullableString;

/**
 * Default implementation of {@link StorIOSQLite} for {@link android.database.sqlite.SQLiteDatabase}.
 * <p/>
 * Thread-safe.
 */
public class DefaultStorIOSQLite extends StorIOSQLite {

    @NonNull
    private final SQLiteOpenHelper sqLiteOpenHelper;

    @NonNull
    private final ChangesBus<Changes> changesBus = new ChangesBus<Changes>();

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
     * <p/>
     * All calls to this instance of {@link StorIOSQLite}
     * after call to this method can produce exceptions
     * and undefined behavior.
     */
    @Override
    public void close() throws IOException {
        sqLiteOpenHelper.close();
    }

    /**
     * Builder for {@link DefaultStorIOSQLite}.
     */
    public static final class Builder {

        /**
         * Required: Specifies SQLite Open helper for internal usage.
         * <p/>
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

        @Nullable
        private final Map<Class<?>, SQLiteTypeMapping<?>> typesMapping;

        /**
         * Guarded by {@link #lock}.
         */
        private int numberOfRunningTransactions = 0;

        /**
         * Guarded by {@link #lock}.
         */
        @NonNull
        private final Set<Changes> pendingChanges = new HashSet<Changes>();

        protected InternalImpl(@Nullable Map<Class<?>, SQLiteTypeMapping<?>> typesMapping) {
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
        public <T> SQLiteTypeMapping<T> typeMapping(@NonNull Class<T> type) {
            return typesMapping != null
                    ? (SQLiteTypeMapping<T>) typesMapping.get(type)
                    : null;
        }

        /**
         * {@inheritDoc}
         */
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
            synchronized (lock) {
                pendingChanges.add(changes);
                notifyAboutPendingChangesIfNotInTransaction();
            }
        }

        /**
         * Access to this method MUST BE guarded by synchronization on {@link #lock}.
         */
        private void notifyAboutPendingChangesIfNotInTransaction() {
            if (numberOfRunningTransactions == 0) {
                for (Changes changes : pendingChanges) {
                    pendingChanges.remove(changes);
                    changesBus.onNext(changes);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void beginTransaction() {
            synchronized (lock) {
                sqLiteOpenHelper
                        .getWritableDatabase()
                        .beginTransaction();

                numberOfRunningTransactions++;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTransactionSuccessful() {
            synchronized (lock) {
                sqLiteOpenHelper
                        .getWritableDatabase()
                        .setTransactionSuccessful();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void endTransaction() {
            synchronized (lock) {
                sqLiteOpenHelper
                        .getWritableDatabase()
                        .endTransaction();

                numberOfRunningTransactions--;
                notifyAboutPendingChangesIfNotInTransaction();
            }
        }
    }
}
