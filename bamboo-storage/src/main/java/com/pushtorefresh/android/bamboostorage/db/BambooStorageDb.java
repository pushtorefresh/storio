package com.pushtorefresh.android.bamboostorage.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.operation.Changes;
import com.pushtorefresh.android.bamboostorage.db.operation.delete.PreparedDelete;
import com.pushtorefresh.android.bamboostorage.db.operation.exec_sql.PreparedExecSql;
import com.pushtorefresh.android.bamboostorage.db.operation.get.PreparedGet;
import com.pushtorefresh.android.bamboostorage.db.operation.put.PreparedPut;
import com.pushtorefresh.android.bamboostorage.db.query.DeleteQuery;
import com.pushtorefresh.android.bamboostorage.db.query.InsertQuery;
import com.pushtorefresh.android.bamboostorage.db.query.Query;
import com.pushtorefresh.android.bamboostorage.db.query.RawQuery;
import com.pushtorefresh.android.bamboostorage.db.query.UpdateQuery;

import java.util.Collections;
import java.util.Set;

import rx.Observable;

/**
 * Powerful abstraction for databases
 * <p/>
 * It's an abstract class instead of interface because we want to have ability to add some
 * changes without breaking existing implementations
 */
public abstract class BambooStorageDb {

    /**
     * Prepares "execute sql" operation for BambooStorage
     * Allows to execute a single SQL statement that is NOT a SELECT/INSERT/UPDATE/DELETE.
     *
     * @return builder for PreparedExecSql
     */
    @NonNull public abstract PreparedExecSql.Builder execSql();

    /**
     * Prepares "get" operation for BambooStorage
     * Allows to get information from BambooStorage
     *
     * @return builder for PreparedGet
     */
    @NonNull public abstract PreparedGet.Builder get();

    /**
     * Prepares "put" operation for BambooStorage
     * Allows to insert/update information in BambooStorage
     *
     * @return builder for PreparedPut
     */
    @NonNull public abstract PreparedPut.Builder put();

    /**
     * Prepares "delete" operation for BambooStorage
     * Allows to delete information from BambooStorage
     *
     * @return builder for PreparedDelete
     */
    @NonNull public abstract PreparedDelete.Builder delete();

    /**
     * Subscribes to changes in required tables
     *
     * @param tables set of table names that should be monitored
     * @return {@link rx.Observable} of {@link Changes} subscribed to changes in required tables
     */
    @NonNull
    public abstract Observable<Changes> observeChangesInTables(@NonNull Set<String> tables);

    /**
     * Subscribes to changes in required table
     *
     * @param table table name to monitor
     * @return {@link rx.Observable} of {@link Changes} subscribed to changes in required table
     */
    @NonNull
    public Observable<Changes> observeChangesInTable(@NonNull String table) {
        return observeChangesInTables(Collections.singleton(table));
    }

    /**
     * Hides some internal operations for BambooStorage to make API of BambooStorage clean and easy to understand
     *
     * @return implementation of Internal operations for BambooStorage
     */
    @NonNull public abstract Internal internal();

    /**
     * Hides some internal operations for BambooStorage
     * to make API of BambooStorage clean and easy to understand
     */
    public static abstract class Internal {

        /**
         * Execute a single SQL statement that is NOT a SELECT/INSERT/UPDATE/DELETE on the database
         *
         * @param rawQuery sql query
         */
        public abstract void execSql(@NonNull RawQuery rawQuery);

        /**
         * Executes raw query on the database and returns {@link android.database.Cursor} over the result set
         *
         * @param rawQuery sql query
         * @return A Cursor object, which is positioned before the first entry. Note that Cursors are not synchronized, see the documentation for more details.
         */
        @NonNull public abstract Cursor rawQuery(@NonNull RawQuery rawQuery);

        /**
         * Executes query on the database and returns {@link android.database.Cursor} over the result set
         *
         * @param query sql query
         * @return A Cursor object, which is positioned before the first entry. Note that Cursors are not synchronized, see the documentation for more details.
         */
        @NonNull public abstract Cursor query(@NonNull Query query);

        /**
         * Inserts a row into the database
         *
         * @param insertQuery   query
         * @param contentValues map that contains the initial column values for the row. The keys should be the column names and the values the column values
         * @return id of inserted row
         */
        public abstract long insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues);

        /**
         * Updates one or multiple rows in the database
         *
         * @param updateQuery   query
         * @param contentValues a map from column names to new column values. null is a valid value that will be translated to NULL.
         * @return the number of rows affected
         */
        public abstract int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues);

        /**
         * Deletes one or multiple rows in the database
         *
         * @param deleteQuery query
         * @return the number of rows deleted
         */
        public abstract int delete(@NonNull DeleteQuery deleteQuery);

        /**
         * Notifies subscribers about changes happened in {@link BambooStorageDb}
         * Operations can be executed in transaction or one operation can affect multiple tables, so to reduce number of notifications
         * you can call this method once and provide Changes object
         *
         * @param changes changes happened in {@link BambooStorageDb}
         */
        public abstract void notifyAboutChanges(@NonNull Changes changes);

        /**
         * BambooStorage implementation could not provide support for transactions
         *
         * @return true if transactions are supported, false otherwise
         */
        public abstract boolean areTransactionsSupported();

        /**
         * Begins a transaction in EXCLUSIVE mode
         */
        public abstract void beginTransaction();

        /**
         * Marks the current transaction as successful
         */
        public abstract void setTransactionSuccessful();

        /**
         * End a transaction
         */
        public abstract void endTransaction();
    }
}
