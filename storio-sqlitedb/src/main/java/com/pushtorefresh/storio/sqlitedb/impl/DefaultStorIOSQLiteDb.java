package com.pushtorefresh.storio.sqlitedb.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlitedb.Changes;
import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.query.DeleteQuery;
import com.pushtorefresh.storio.sqlitedb.query.InsertQuery;
import com.pushtorefresh.storio.sqlitedb.query.Query;
import com.pushtorefresh.storio.sqlitedb.query.RawQuery;
import com.pushtorefresh.storio.sqlitedb.query.UpdateQuery;
import com.pushtorefresh.storio.util.EnvironmentUtil;
import com.pushtorefresh.storio.util.QueryUtil;

import java.util.Set;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Default implementation of {@link StorIOSQLiteDb} for {@link SQLiteDatabase}
 * <p>
 * Thread safe
 */
public class DefaultStorIOSQLiteDb extends StorIOSQLiteDb {

    /**
     * Real db
     */
    @NonNull
    private final SQLiteDatabase db;

    /**
     * Reactive bus for notifying observers about changes in StorIOSQLiteDb
     * One change can affect several tables, so we use {@link Changes} as representation of changes
     */
    @Nullable
    private final PublishSubject<Changes> changesBus = EnvironmentUtil.IS_RX_JAVA_AVAILABLE
            ? PublishSubject.<Changes>create()
            : null;

    /**
     * Implementation of {@link StorIOSQLiteDb.Internal}
     */
    @NonNull
    private final Internal internal = new InternalImpl();

    protected DefaultStorIOSQLiteDb(@NonNull SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    @NonNull
    public Observable<Changes> observeChangesInTables(@NonNull final Set<String> tables) {
        if (changesBus == null) {
            throw EnvironmentUtil.newRxJavaIsNotAvailableException("Observing changes in StorIOSQLiteDb");
        }

        return changesBus
                .filter(new Func1<Changes, Boolean>() {
                    @Override
                    public Boolean call(Changes changes) {
                        // if one of changed tables found in tables for subscription -> notify observer
                        for (String affectedTable : changes.affectedTables()) {
                            if (tables.contains(affectedTable)) {
                                return true;
                            }
                        }

                        return false;
                    }
                });
    }

    @NonNull
    @Override
    public Internal internal() {
        return internal;
    }

    protected class InternalImpl extends Internal {

        @Override
        public void execSql(@NonNull RawQuery rawQuery) {
            db.execSQL(rawQuery.query, QueryUtil.listToArray(rawQuery.args));
        }

        @NonNull
        @Override
        public Cursor rawQuery(@NonNull RawQuery rawQuery) {
            return db.rawQuery(
                    rawQuery.query,
                    QueryUtil.listToArray(rawQuery.args)
            );
        }

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

        @Override
        public long insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues) {
            return db.insertOrThrow(
                    insertQuery.table,
                    insertQuery.nullColumnHack,
                    contentValues
            );
        }

        @Override
        public int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues) {
            return db.update(
                    updateQuery.table,
                    contentValues,
                    updateQuery.where,
                    QueryUtil.listToArray(updateQuery.whereArgs)
            );
        }

        @Override
        public int delete(@NonNull DeleteQuery deleteQuery) {
            return db.delete(
                    deleteQuery.table,
                    deleteQuery.where,
                    QueryUtil.listToArray(deleteQuery.whereArgs)
            );
        }

        @Override
        public void notifyAboutChanges(@NonNull Changes changes) {
            // Notifying about changes requires RxJava, if RxJava is not available -> skip notification
            if (changesBus != null) {
                changesBus.onNext(changes);
            }
        }

        @Override
        public boolean transactionsSupported() {
            return true;
        }

        @Override
        public void beginTransaction() {
            db.beginTransaction();
        }

        @Override
        public void setTransactionSuccessful() {
            db.setTransactionSuccessful();
        }

        @Override
        public void endTransaction() {
            db.endTransaction();
        }
    }

    /**
     * Builder for {@link DefaultStorIOSQLiteDb}
     */
    public static class Builder {

        private SQLiteDatabase db;

        /**
         * Specifies database for internal usage.
         * You should provide this or {@link SQLiteOpenHelper}
         * @see {@link #sqliteOpenHelper(SQLiteOpenHelper)}
         *
         * @param db a real database for internal usage
         * @return builder
         */
        @NonNull
        public Builder db(@NonNull SQLiteDatabase db) {
            this.db = db;
            return this;
        }

        /**
         * Specifies SqLite helper for internal usage
         * You should provide this or {@link SQLiteDatabase}
         * @see {@link #db(SQLiteDatabase)}
         *
         * @param sqliteOpenHelper a SqLite helper for internal usage
         * @return builder
         */
        @NonNull
        public Builder sqliteOpenHelper(@NonNull SQLiteOpenHelper sqliteOpenHelper) {
            db = sqliteOpenHelper.getWritableDatabase();
            return this;
        }

        /**
         * Builds {@link DefaultStorIOSQLiteDb} instance with required params
         *
         * @return new {@link DefaultStorIOSQLiteDb} instance
         */

        @NonNull
        public DefaultStorIOSQLiteDb build() {
            checkNotNull(db, "Please specify SQLiteDatabase instance");

            return new DefaultStorIOSQLiteDb(db);
        }
    }
}
