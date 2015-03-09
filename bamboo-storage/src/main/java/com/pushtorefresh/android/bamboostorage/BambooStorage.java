package com.pushtorefresh.android.bamboostorage;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.operation.delete.PreparedDelete;
import com.pushtorefresh.android.bamboostorage.operation.exec_sql.PreparedExecSql;
import com.pushtorefresh.android.bamboostorage.operation.get.PreparedGet;
import com.pushtorefresh.android.bamboostorage.operation.put.PreparedPut;
import com.pushtorefresh.android.bamboostorage.query.DeleteQuery;
import com.pushtorefresh.android.bamboostorage.query.InsertQuery;
import com.pushtorefresh.android.bamboostorage.query.Query;
import com.pushtorefresh.android.bamboostorage.query.RawQuery;
import com.pushtorefresh.android.bamboostorage.query.UpdateQuery;

import java.util.Set;

import rx.Observable;

public interface BambooStorage {

    @NonNull PreparedExecSql.Builder execSql();

    @NonNull PreparedGet.Builder get();

    @NonNull PreparedPut.Builder put();

    @NonNull PreparedDelete.Builder delete();

    @NonNull Observable<Set<String>> subscribeOnChanges(@NonNull Set<String> tables);

    @NonNull Internal internal();

    interface Internal {

        /**
         * Execute a single SQL statement that is NOT a SELECT/INSERT/UPDATE/DELETE.
         * @param rawQuery sql query
         */
        void execSql(@NonNull RawQuery rawQuery);

        @NonNull Cursor rawQuery(@NonNull RawQuery rawQuery);

        @NonNull Cursor query(@NonNull Query query);

        long insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues);

        int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues);

        int delete(@NonNull DeleteQuery deleteQuery);

        void notifyAboutChanges(@NonNull Set<String> affectedTables);

        boolean areTransactionsSupported();

        void beginTransaction();

        void setTransactionSuccessful();

        void endTransaction();
    }
}
