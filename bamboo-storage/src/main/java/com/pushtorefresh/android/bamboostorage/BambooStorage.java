package com.pushtorefresh.android.bamboostorage;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.query.DeleteQuery;
import com.pushtorefresh.android.bamboostorage.query.InsertQuery;
import com.pushtorefresh.android.bamboostorage.query.Query;
import com.pushtorefresh.android.bamboostorage.query.UpdateQuery;

public interface BambooStorage {

    @NonNull PreparedQuery.Builder prepareQuery(@NonNull Query query);

    @NonNull Internal getInternal();

    interface Internal {
        @NonNull Cursor query(@NonNull Query query);

        long insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues);

        int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues);

        int delete(@NonNull DeleteQuery deleteQuery);
    }
}
