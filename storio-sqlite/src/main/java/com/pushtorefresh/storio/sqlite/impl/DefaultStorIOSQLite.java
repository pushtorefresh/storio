package com.pushtorefresh.storio.sqlite.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;
import com.pushtorefresh.storio.sqlite.query.InsertQuery;
import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;
import com.pushtorefresh.storio.sqlite.query.UpdateQuery;
import com.pushtorefresh.storio.util.EnvironmentUtil;
import com.pushtorefresh.storio.util.QueryUtil;

import java.util.Set;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;
import static com.pushtorefresh.storio.util.EnvironmentUtil.newRxJavaIsNotAvailableException;

/**
 * Default implementation of {@link StorIOSQLite} for {@link SQLiteDatabase}
 * <p>
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
    private final Internal internal = new InternalImpl();

    protected DefaultStorIOSQLite(@NonNull SQLiteDatabase db) {
        this.db = db;
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

    protected class InternalImpl extends Internal {

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

    /**
     * Builder for {@link DefaultStorIOSQLite}
     */
    public static class Builder {

        SQLiteDatabase db;

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
            this.db = db;
            return new CompleteBuilder(this);
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
            db = sqliteOpenHelper.getWritableDatabase();
            return new CompleteBuilder(this);
        }
    }

    /**
     * Compile-time safe part of builder for {@link DeleteQuery}
     */
    public static class CompleteBuilder extends Builder {

        CompleteBuilder(@NonNull Builder builder) {
            db = builder.db;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public CompleteBuilder db(@NonNull SQLiteDatabase db) {
            this.db = db;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public CompleteBuilder sqliteOpenHelper(@NonNull SQLiteOpenHelper sqliteOpenHelper) {
            db = sqliteOpenHelper.getWritableDatabase();
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
            return new DefaultStorIOSQLite(db);
        }
    }
}
