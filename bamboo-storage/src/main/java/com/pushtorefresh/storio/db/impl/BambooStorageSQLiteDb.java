package com.pushtorefresh.storio.db.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.BambooStorageDb;
import com.pushtorefresh.storio.db.operation.Changes;
import com.pushtorefresh.storio.db.operation.delete.PreparedDelete;
import com.pushtorefresh.storio.db.operation.exec_sql.PreparedExecSql;
import com.pushtorefresh.storio.db.operation.get.PreparedGet;
import com.pushtorefresh.storio.db.operation.put.PreparedPut;
import com.pushtorefresh.storio.db.query.DeleteQuery;
import com.pushtorefresh.storio.db.query.InsertQuery;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;
import com.pushtorefresh.storio.db.query.UpdateQuery;

import java.util.Set;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class BambooStorageSQLiteDb extends BambooStorageDb {

    /**
     * Real db
     */
    @NonNull private final SQLiteDatabase db;

    /**
     * Reactive bus for notifying observers about changes in BambooStorageDb
     * One change can affect several tables, so we use Set<String> as set of changed tables per event
     */
    @NonNull private final PublishSubject<Changes> changesBus = PublishSubject.create();

    /**
     * Implementation of {@link BambooStorageDb.Internal}
     */
    @NonNull private final Internal internal = new InternalImpl();

    protected BambooStorageSQLiteDb(@NonNull SQLiteDatabase db) {
        this.db = db;
    }

    @NonNull @Override public PreparedExecSql.Builder execSql() {
        return new PreparedExecSql.Builder(this);
    }

    @NonNull @Override public PreparedGet.Builder get() {
        return new PreparedGet.Builder(this);
    }

    @NonNull @Override public PreparedPut.Builder put() {
        return new PreparedPut.Builder(this);
    }

    @NonNull @Override public PreparedDelete.Builder delete() {
        return new PreparedDelete.Builder(this);
    }

    @Override @NonNull
    public Observable<Changes> observeChangesInTables(@NonNull final Set<String> tables) {
        return changesBus
                .filter(new Func1<Changes, Boolean>() {
                    @Override public Boolean call(Changes changes) {
                        // if one of changed tables found in tables for subscription -> notify observer
                        for (String changedTable : changes.tables()) {
                            if (tables.contains(changedTable)) {
                                return true;
                            }
                        }

                        return false;
                    }
                });
    }

    @NonNull @Override public Internal internal() {
        return internal;
    }

    protected class InternalImpl extends Internal {

        @Override public void execSql(@NonNull RawQuery rawQuery) {
            db.execSQL(rawQuery.query, rawQuery.args);
        }

        @NonNull @Override public Cursor rawQuery(@NonNull RawQuery rawQuery) {
            return db.rawQuery(
                    rawQuery.query,
                    rawQuery.args
            );
        }

        @NonNull @Override public Cursor query(@NonNull Query query) {
            return db.query(
                    query.distinct,
                    query.table,
                    query.columns,
                    query.selection,
                    query.whereArgs,
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
                    updateQuery.whereArgs
            );
        }

        @Override public int delete(@NonNull DeleteQuery deleteQuery) {
            return db.delete(
                    deleteQuery.table,
                    deleteQuery.where,
                    deleteQuery.whereArgs
            );
        }

        @Override public void notifyAboutChanges(@NonNull Changes changes) {
            changesBus.onNext(changes);
        }

        @Override public boolean areTransactionsSupported() {
            return true;
        }

        @Override public void beginTransaction() {
            db.beginTransaction();
        }

        @Override public void setTransactionSuccessful() {
            db.setTransactionSuccessful();
        }

        @Override public void endTransaction() {
            db.endTransaction();
        }
    }

    public static class Builder {

        private SQLiteDatabase db;

        @NonNull public Builder db(@NonNull SQLiteDatabase db) {
            this.db = db;
            return this;
        }

        @NonNull public Builder sqliteOpenHelper(@NonNull SQLiteOpenHelper sqliteOpenHelper) {
            db = sqliteOpenHelper.getWritableDatabase();
            return this;
        }

        @NonNull public BambooStorageSQLiteDb build() {
            if (db == null) {
                throw new IllegalStateException("Please specify SQLiteDatabase instance");
            }

            return new BambooStorageSQLiteDb(db);
        }
    }
}
