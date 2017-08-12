package com.pushtorefresh.storio2.sample.sqldelight;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio2.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;
import com.squareup.sqldelight.RowMapper;
import com.squareup.sqldelight.SqlDelightStatement;

import java.util.ArrayList;
import java.util.List;

import static com.pushtorefresh.storio2.sqlite.operations.put.PutResult.newInsertResult;

public final class SQLUtils {

    private SQLUtils() {
    }

    @NonNull
    public static PutResolver<ContentValues> makeSimpleContentValuesInsertPutResolver(@NonNull final String tableName) {
        return new PutResolver<ContentValues>() {
            @NonNull
            final InsertQuery insert = InsertQuery.builder()
                    .table(tableName)
                    .build();

            @NonNull
            @Override
            public PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull ContentValues contentValues) {
                final long insertedId = storIOSQLite.lowLevel().insert(insert, contentValues);
                return newInsertResult(insertedId, tableName);
            }
        };
    }

    @NonNull
    public static RawQuery makeReadQuery(@NonNull SqlDelightStatement statement) {
        return RawQuery.builder()
                .query(statement.statement)
                .args(statement.args)
                .observesTables(statement.tables)
                .build();
    }

    @NonNull
    public static RawQuery makeWriteQuery(@NonNull SqlDelightStatement statement) {
        return RawQuery.builder()
                .query(statement.statement)
                .args(statement.args)
                .affectsTables(statement.tables)
                .build();
    }

    @NonNull
    public static <T> List<T> mapFromCursor(@NonNull Cursor cursor, @NonNull RowMapper<? extends T> mapper) {
        try {
            final List<T> result = new ArrayList<T>(cursor.getCount());
            while (cursor.moveToNext()) {
                result.add(mapper.map(cursor));
            }
            return result;
        } finally {
            cursor.close();
        }
    }
}
