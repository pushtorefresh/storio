package com.pushtorefresh.storio.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.sqlite.operation.delete.PreparedDelete;
import com.pushtorefresh.storio.sqlite.operation.execute.PreparedExecuteSQL;
import com.pushtorefresh.storio.sqlite.operation.get.PreparedGet;
import com.pushtorefresh.storio.sqlite.operation.put.PreparedPut;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;
import com.pushtorefresh.storio.sqlite.query.InsertQuery;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;
import com.pushtorefresh.storio.sqlite.query.UpdateQuery;

import java.io.Closeable;
import java.util.Collections;
import java.util.Set;

import rx.Observable;

/**
 * Powerful but simple abstraction for {@link android.database.sqlite.SQLiteDatabase}.
 * <p/>
 * It's an abstract class instead of interface because we want to have ability to add some
 * changes without breaking existing implementations.
 */
public abstract class StorIOSQLite implements Closeable {

    /**
     * Prepares "Execute SQL" Operation for {@link StorIOSQLite}.
     * Allows to execute a single SQL statement that is NOT a SELECT/INSERT/UPDATE/DELETE.
     *
     * @return builder for {@link PreparedExecuteSQL}.
     */
    @NonNull
    public PreparedExecuteSQL.Builder executeSQL() {
        return new PreparedExecuteSQL.Builder(this);
    }

    /**
     * Prepares "Get" Operation for {@link StorIOSQLite}.
     * Allows to get information from {@link StorIOSQLite}.
     *
     * @return builder for {@link PreparedGet}.
     */
    @NonNull
    public PreparedGet.Builder get() {
        return new PreparedGet.Builder(this);
    }

    /**
     * Prepares "Put" Operation for {@link StorIOSQLite}.
     * Allows to insert/update information in {@link StorIOSQLite}.
     *
     * @return builder for {@link PreparedPut}.
     */
    @NonNull
    public PreparedPut.Builder put() {
        return new PreparedPut.Builder(this);
    }

    /**
     * Prepares "Delete" Operation for {@link StorIOSQLite}.
     * Allows to delete information from {@link StorIOSQLite}.
     *
     * @return builder for {@link PreparedDelete}.
     */
    @NonNull
    public PreparedDelete.Builder delete() {
        return new PreparedDelete.Builder(this);
    }

    /**
     * Subscribes to changes of required tables.
     *
     * @param tables set of table names that should be monitored.
     * @return {@link rx.Observable} of {@link Changes} subscribed to changes of required tables.
     */
    @NonNull
    public abstract Observable<Changes> observeChangesInTables(@NonNull Set<String> tables);

    /**
     * Subscribes to changes of required table.
     *
     * @param table table name to monitor.
     * @return {@link rx.Observable} of {@link Changes} subscribed to changes of required table.
     */
    @NonNull
    public Observable<Changes> observeChangesInTable(@NonNull String table) {
        return observeChangesInTables(Collections.singleton(table));
    }

    /**
     * Hides some internal operations of {@link StorIOSQLite}
     * to make API of {@link StorIOSQLite} clean and easy to understand.
     *
     * @return implementation of Internal operations for {@link StorIOSQLite}.
     */
    @NonNull
    public abstract Internal internal();

    /**
     * Hides some internal operations of {@link StorIOSQLite}
     * to make {@link StorIOSQLite} API clean and easy to understand.
     */
    public static abstract class Internal {

        /**
         * Gets {@link SQLiteTypeMapping} for required type.
         * <p/>
         * Result can be {@code null}.
         *
         * @param type type.
         * @param <T>  type.
         * @return {@link SQLiteTypeMapping} for required type or {@code null}.
         */
        @Nullable
        public abstract <T> SQLiteTypeMapping<T> typeMapping(@NonNull Class<T> type);

        /**
         * Executes a single SQL statement that
         * is NOT a SELECT/INSERT/UPDATE/DELETE on the database.
         *
         * @param rawQuery sql query.
         */
        @WorkerThread
        public abstract void executeSQL(@NonNull RawQuery rawQuery);

        /**
         * Executes raw query on the database
         * and returns {@link android.database.Cursor} over the result set.
         *
         * @param rawQuery sql query
         * @return A Cursor object, which is positioned before the first entry.
         * Note that Cursors are not synchronized, see the documentation for more details.
         */
        @WorkerThread
        @NonNull
        public abstract Cursor rawQuery(@NonNull RawQuery rawQuery);

        /**
         * Executes query on the database and returns {@link android.database.Cursor}
         * over the result set.
         *
         * @param query sql query.
         * @return A Cursor object, which is positioned before the first entry.
         * Note that Cursors are not synchronized, see the documentation for more details.
         */
        @WorkerThread
        @NonNull
        public abstract Cursor query(@NonNull Query query);

        /**
         * Inserts a row into the database.
         *
         * @param insertQuery   query.
         * @param contentValues map that contains the initial column values for the row.
         *                      The keys should be the column names and the values the column values.
         * @return id of inserted row.
         */
        @WorkerThread
        public abstract long insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues);

        /**
         * Updates one or multiple rows in the database.
         *
         * @param updateQuery   query.
         * @param contentValues a map from column names to new column values.
         *                      {@code null} is a valid value that will be translated to {@code NULL}.
         * @return the number of rows affected.
         */
        @WorkerThread
        public abstract int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues);

        /**
         * Deletes one or multiple rows in the database.
         *
         * @param deleteQuery query.
         * @return the number of rows deleted.
         */
        @WorkerThread
        public abstract int delete(@NonNull DeleteQuery deleteQuery);

        /**
         * Notifies subscribers about changes happened in {@link StorIOSQLite}.
         * Operations can be executed in transaction or one operation can affect multiple tables,
         * so to reduce number of notifications you can call this method once and
         * provide aggregated Changes object.
         *
         * @param changes changes happened in {@link StorIOSQLite}.
         */
        public abstract void notifyAboutChanges(@NonNull Changes changes);

        /**
         * Begins a transaction in EXCLUSIVE mode.
         * <p/>
         * Thread will be blocked on call to this method if another thread already in transaction,
         * as soon as first thread will end its transaction this thread will be unblocked.
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
         * Ends a transaction. See {@link #beginTransaction()} for notes about
         * how to use this and when transactions are committed and rolled back.
         */
        public abstract void endTransaction();
    }
}
