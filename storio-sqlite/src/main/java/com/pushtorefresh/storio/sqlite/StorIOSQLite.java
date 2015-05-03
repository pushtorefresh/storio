package com.pushtorefresh.storio.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.operation.delete.PreparedDelete;
import com.pushtorefresh.storio.sqlite.operation.exec_sql.PreparedExecSql;
import com.pushtorefresh.storio.sqlite.operation.get.PreparedGet;
import com.pushtorefresh.storio.sqlite.operation.put.PreparedPut;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;
import com.pushtorefresh.storio.sqlite.query.InsertQuery;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;
import com.pushtorefresh.storio.sqlite.query.UpdateQuery;

import java.util.Collections;
import java.util.Set;

import rx.Observable;

/**
 * Powerful abstraction for databases
 * <p/>
 * It's an abstract class instead of interface because we want to have ability to add some
 * changes without breaking existing implementations
 */
public abstract class StorIOSQLite {

    /**
     * Prepares "execute sql" operation for {@link StorIOSQLite}
     * Allows to execute a single SQL statement that is NOT a SELECT/INSERT/UPDATE/DELETE.
     *
     * @return builder for PreparedExecSql
     */
    @NonNull
    public PreparedExecSql.Builder execSql() {
        return new PreparedExecSql.Builder(this);
    }

    /**
     * Prepares "get" operation for {@link StorIOSQLite}
     * Allows to get information from {@link StorIOSQLite}
     *
     * @return builder for PreparedGet
     */
    @NonNull
    public PreparedGet.Builder get() {
        return new PreparedGet.Builder(this);
    }

    /**
     * Prepares "put" operation for {@link StorIOSQLite}
     * Allows to insert/update information in {@link StorIOSQLite}
     *
     * @return builder for PreparedPut
     */
    @NonNull
    public PreparedPut.Builder put() {
        return new PreparedPut.Builder(this);
    }

    /**
     * Prepares "delete" operation for {@link StorIOSQLite}
     * Allows to delete information from {@link StorIOSQLite}
     *
     * @return builder for PreparedDelete
     */
    @NonNull
    public PreparedDelete.Builder delete() {
        return new PreparedDelete.Builder(this);
    }

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
     * Hides some internal operations of {@link StorIOSQLite} to make API of {@link StorIOSQLite} clean and easy to understand
     *
     * @return implementation of Internal operations for {@link StorIOSQLite}
     */
    @NonNull
    public abstract Internal internal();

    /**
     * Hides some internal operations of {@link StorIOSQLite}
     * to make {@link StorIOSQLite} API clean and easy to understand
     */
    public static abstract class Internal {

        /**
         * Gets {@link SQLiteTypeDefaults} for required type
         * <p/>
         * Result can be null
         *
         * @param type type
         * @param <T>  type
         * @return {@link SQLiteTypeDefaults} for required type or null
         */
        @Nullable
        public abstract <T> SQLiteTypeDefaults<T> typeDefaults(@NonNull Class<T> type);

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
        @NonNull
        public abstract Cursor rawQuery(@NonNull RawQuery rawQuery);

        /**
         * Executes query on the database and returns {@link android.database.Cursor} over the result set
         *
         * @param query sql query
         * @return A Cursor object, which is positioned before the first entry. Note that Cursors are not synchronized, see the documentation for more details.
         */
        @NonNull
        public abstract Cursor query(@NonNull Query query);

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
         * Notifies subscribers about changes happened in {@link StorIOSQLite}
         * Operations can be executed in transaction or one operation can affect multiple tables, so to reduce number of notifications
         * you can call this method once and provide Changes object
         *
         * @param changes changes happened in {@link StorIOSQLite}
         */
        public abstract void notifyAboutChanges(@NonNull Changes changes);

        /**
         * Begins a transaction in EXCLUSIVE mode.
         * <p/>
         * Thread will be blocked on call to this method if another thread already in transaction,
         * as soon as first thread will end its transaction this thread will be unblocked
         * <p>
         * Transactions can be nested.
         * When the outer transaction is ended all of
         * the work done in that transaction and all of the nested transactions will be committed or
         * rolled back. The changes will be rolled back if any transaction is ended without being
         * marked as clean (by calling setTransactionSuccessful). Otherwise they will be committed.
         * </p>
         * <p>Here is the standard idiom for transactions:
         * <p/>
         * <pre>
         *   db.beginTransaction();
         *   try {
         *     ...
         *     db.setTransactionSuccessful();
         *   } finally {
         *     db.endTransaction();
         *   }
         * </pre>
         */
        public abstract void beginTransaction();

        /**
         * Marks the current transaction as successful. Do not do any more database work between
         * calling this and calling endTransaction. Do as little non-database work as possible in that
         * situation too. If any errors are encountered between this and endTransaction the transaction
         * will still be committed.
         *
         * @throws IllegalStateException if the transaction is already marked as successful.
         */
        public abstract void setTransactionSuccessful();

        /**
         * Ends a transaction. See {@link #beginTransaction()} for notes about how to use this and when transactions
         * are committed and rolled back.
         */
        public abstract void endTransaction();
    }
}
