package com.pushtorefresh.storio.sqlite.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.internal.ChangesBus;
import com.pushtorefresh.storio.internal.Queries;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeDefaults;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;
import com.pushtorefresh.storio.sqlite.query.InsertQuery;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;
import com.pushtorefresh.storio.sqlite.query.UpdateQuery;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Default implementation of {@link StorIOSQLite} for {@link SQLiteDatabase}.
 * <p>
 * Thread-safe.
 */
public class DefaultStorIOSQLite extends StorIOSQLite {

    @NonNull
    private final SQLiteDatabase db;

    @NonNull
    private final ChangesBus<Changes> changesBus = new ChangesBus<Changes>();

    /**
     * Implementation of {@link StorIOSQLite.Internal}
     */
    @NonNull
    private final Internal internal;

    protected DefaultStorIOSQLite(@NonNull SQLiteDatabase db, @Nullable Map<Class<?>, SQLiteTypeDefaults<?>> typeDefinitionMap) {
        this.db = db;
        internal = new InternalImpl(typeDefinitionMap);
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
     * Builder for {@link DefaultStorIOSQLite}.
     */
    public static final class Builder {

        /**
         * Required: Specifies actual database to use under the hood.
         * <p>
         * You should provide this or {@link SQLiteDatabase}.
         *
         * @param db a real database for internal usage.
         * @return builder.
         * @see #sqliteOpenHelper(SQLiteOpenHelper)
         */
        @NonNull
        public CompleteBuilder db(@NonNull SQLiteDatabase db) {
            checkNotNull(db, "Please specify SQLiteDatabase instance");
            return new CompleteBuilder(db);
        }

        /**
         * Required: Specifies SqLite helper for internal usage.
         * <p>
         * You should provide this or {@link SQLiteDatabase}.
         *
         * @param sqliteOpenHelper a SqLite helper for internal usage.
         * @return builder.
         * @see #db(SQLiteDatabase)
         */
        @NonNull
        public CompleteBuilder sqliteOpenHelper(@NonNull SQLiteOpenHelper sqliteOpenHelper) {
            SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
            checkNotNull(db, "Please specify SQLiteDatabase instance");
            return new CompleteBuilder(db);
        }
    }

    /**
     * Compile-time safe part of builder for {@link DefaultStorIOSQLite}.
     */
    public static final class CompleteBuilder {

        @NonNull
        private final SQLiteDatabase db;

        private Map<Class<?>, SQLiteTypeDefaults<?>> typesDefaultsMap;

        CompleteBuilder(@NonNull SQLiteDatabase db) {
            this.db = db;
        }

        /**
         * Adds {@link SQLiteTypeDefaults} for some type.
         *
         * @param type         type.
         * @param typeDefaults defaults for type.
         * @param <T>          type.
         * @return builder.
         */
        @NonNull
        public <T> CompleteBuilder addDefaultsForType(@NonNull Class<T> type, @NonNull SQLiteTypeDefaults<T> typeDefaults) {
            checkNotNull(type, "Please specify type");
            checkNotNull(typeDefaults, "Please specify type defaults");

            if (typesDefaultsMap == null) {
                typesDefaultsMap = new HashMap<Class<?>, SQLiteTypeDefaults<?>>();
            }

            if (typesDefaultsMap.containsKey(type)) {
                throw new IllegalArgumentException("Defaults for type " + type.getSimpleName() + " already added");
            }

            typesDefaultsMap.put(type, typeDefaults);

            return this;
        }

        /**
         * Builds {@link DefaultStorIOSQLite} instance with required params.
         *
         * @return new {@link DefaultStorIOSQLite} instance.
         */
        @NonNull
        public DefaultStorIOSQLite build() {
            return new DefaultStorIOSQLite(db, typesDefaultsMap);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected class InternalImpl extends Internal {

        @NonNull
        private final Object lock = new Object();

        @Nullable
        private final Map<Class<?>, SQLiteTypeDefaults<?>> typesDefaultsMap;

        /**
         * Guarded by {@link #lock}
          */
        private int numberOfRunningTransactions = 0;

        /**
         * Guarded by {@link #lock}
         */
        @NonNull
        private final Set<Changes> pendingChanges = new HashSet<Changes>();


        protected InternalImpl(@Nullable Map<Class<?>, SQLiteTypeDefaults<?>> typesDefaultsMap) {
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
        public <T> SQLiteTypeDefaults<T> typeDefaults(@NonNull Class<T> type) {
            return typesDefaultsMap != null
                    ? (SQLiteTypeDefaults<T>) typesDefaultsMap.get(type)
                    : null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void executeSQL(@NonNull RawQuery rawQuery) {
            db.execSQL(rawQuery.query(), Queries.listToArray(rawQuery.args()));
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public Cursor rawQuery(@NonNull RawQuery rawQuery) {
            return db.rawQuery(
                    rawQuery.query(),
                    Queries.listToArray(rawQuery.args())
            );
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public Cursor query(@NonNull Query query) {
            return db.query(
                    query.distinct(),
                    query.table(),
                    Queries.listToArray(query.columns()),
                    query.where(),
                    Queries.listToArray(query.whereArgs()),
                    query.groupBy(),
                    query.having(),
                    query.orderBy(),
                    query.limit()
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues) {
            return db.insertOrThrow(
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
            return db.update(
                    updateQuery.table(),
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
            return db.delete(
                    deleteQuery.table(),
                    deleteQuery.where(),
                    Queries.listToArray(deleteQuery.whereArgs())
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
         * Access to this method MUST BE guarded by synchronization on {@link #lock}
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
                db.beginTransaction();
                numberOfRunningTransactions++;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTransactionSuccessful() {
            synchronized (lock) {
                db.setTransactionSuccessful();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void endTransaction() {
            synchronized (lock) {
                db.endTransaction();
                numberOfRunningTransactions--;
                notifyAboutPendingChangesIfNotInTransaction();
            }
        }
    }
}
