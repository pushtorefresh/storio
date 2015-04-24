package com.pushtorefresh.storio.sqlite.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeDefaults;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;
import com.pushtorefresh.storio.sqlite.query.InsertQuery;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;
import com.pushtorefresh.storio.sqlite.query.UpdateQuery;
import com.pushtorefresh.storio.util.EnvironmentUtil;
import com.pushtorefresh.storio.util.QueryUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;
import static com.pushtorefresh.storio.util.EnvironmentUtil.newRxJavaIsNotAvailableException;

/**
 * Default implementation of {@link StorIOSQLite} for {@link SQLiteDatabase}
 * <p/>
 * Thread safe
 */
public class DefaultStorIOSQLite extends StorIOSQLite {

    /**
     * Real db
     */
    @NonNull
    private final SQLiteDatabase db;

    /**
     * Reactive bus for notifying observers about changes in StorIOSQLite
     * One change can affect several tables, so we use {@link Changes} as representation of changes
     */
    @Nullable
    private final PublishSubject<Changes> changesBus = EnvironmentUtil.IS_RX_JAVA_AVAILABLE
            ? PublishSubject.<Changes>create()
            : null;

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
        if (changesBus == null) {
            throw newRxJavaIsNotAvailableException("Observing changes in StorIOSQLite");
        }

        // indirect usage of RxJava filter() required to avoid problems with ClassLoader when RxJava is not in ClassPath
        return ChangesFilter.apply(changesBus, tables);
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
     * Builder for {@link DefaultStorIOSQLite}
     */
    public static class Builder {

        /**
         * Specifies database for internal usage.
         * You should provide this or {@link SQLiteOpenHelper}
         *
         * @param db a real database for internal usage
         * @return builder
         * @see {@link #sqliteOpenHelper(SQLiteOpenHelper)}
         */
        @NonNull
        public CompleteBuilder db(@NonNull SQLiteDatabase db) {
            return new CompleteBuilder(db);
        }

        /**
         * Specifies SqLite helper for internal usage
         * You should provide this or {@link SQLiteDatabase}
         *
         * @param sqliteOpenHelper a SqLite helper for internal usage
         * @return builder
         * @see {@link #db(SQLiteDatabase)}
         */
        @NonNull
        public CompleteBuilder sqliteOpenHelper(@NonNull SQLiteOpenHelper sqliteOpenHelper) {
            return new CompleteBuilder(sqliteOpenHelper.getWritableDatabase());
        }
    }

    /**
     * Compile-time safe part of builder for {@link DefaultStorIOSQLite}
     */
    public static class CompleteBuilder {

        SQLiteDatabase db;
        Map<Class<?>, SQLiteTypeDefaults<?>> typesDefaultsMap;

        CompleteBuilder(@NonNull SQLiteDatabase db) {
            this.db = db;
        }

        /**
         * Adds {@link SQLiteTypeDefaults} for some type
         *
         * @param type         type
         * @param typeDefaults defaults for type
         * @param <T>          type
         * @return builder
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
         * Builds {@link DefaultStorIOSQLite} instance with required params
         *
         * @return new {@link DefaultStorIOSQLite} instance
         */
        @NonNull
        public DefaultStorIOSQLite build() {
            checkNotNull(db, "Please specify SQLiteDatabase instance");
            return new DefaultStorIOSQLite(db, typesDefaultsMap);
        }
    }

    protected class InternalImpl extends Internal {

        @Nullable
        private final Map<Class<?>, SQLiteTypeDefaults<?>> typesDefaultsMap;

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
        public void execSql(@NonNull RawQuery rawQuery) {
            db.execSQL(rawQuery.query, QueryUtil.listToArray(rawQuery.args));
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public Cursor rawQuery(@NonNull RawQuery rawQuery) {
            return db.rawQuery(
                    rawQuery.query,
                    QueryUtil.listToArray(rawQuery.args)
            );
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public Cursor query(@NonNull Query query) {
            return db.query(
                    query.distinct,
                    query.table,
                    QueryUtil.listToArray(query.columns),
                    query.where,
                    QueryUtil.listToArray(query.whereArgs),
                    query.groupBy,
                    query.having,
                    query.orderBy,
                    query.limit
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues) {
            return db.insertOrThrow(
                    insertQuery.table,
                    insertQuery.nullColumnHack,
                    contentValues
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues) {
            return db.update(
                    updateQuery.table,
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
            return db.delete(
                    deleteQuery.table,
                    deleteQuery.where,
                    QueryUtil.listToArray(deleteQuery.whereArgs)
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void notifyAboutChanges(@NonNull Changes changes) {
            // Notifying about changes requires RxJava, if RxJava is not available -> skip notification
            if (changesBus != null) {
                changesBus.onNext(changes);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean transactionsSupported() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void beginTransaction() {
            db.beginTransaction();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTransactionSuccessful() {
            db.setTransactionSuccessful();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void endTransaction() {
            db.endTransaction();
        }
    }
}
