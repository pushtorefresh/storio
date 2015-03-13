package com.pushtorefresh.storio.db.unit_test.impl;

import android.database.sqlite.SQLiteDatabase;

import com.pushtorefresh.storio.db.BambooStorageDb;
import com.pushtorefresh.storio.db.impl.BambooStorageSQLiteDb;
import com.pushtorefresh.storio.db.query.RawQuery;
import com.pushtorefresh.storio.db.query.RawQueryBuilder;

import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BSSQLiteDatabaseTest {

    @Test public void internalExecSql() {
        final SQLiteDatabase db = mock(SQLiteDatabase.class);

        final BambooStorageDb bambooStorageDb = new BambooStorageSQLiteDb.Builder()
                .db(db)
                .build();

        final RawQuery rawQuery = new RawQueryBuilder().query("ALTER TABLE users").build();

        bambooStorageDb.internal().execSql(rawQuery);

        verify(db, times(1)).execSQL(eq(rawQuery.query), eq(rawQuery.args));
    }
}
