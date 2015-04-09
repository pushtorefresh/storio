package com.pushtorefresh.storio.test_without_rxjava.sqlite;

import android.database.sqlite.SQLiteDatabase;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operation.get.GetResolver;
import com.pushtorefresh.storio.sqlite.query.Query;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DefaultStorIOSQLiteTest {

    @Test
    public void instantiateWithoutRxJava() {
        // Should not fail
        new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .build();
    }

    @Test
    public void instantiateGetCursor() {
        new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .build()
                .get()
                .cursor()
                .withGetResolver(mock(GetResolver.class))
                .withQuery(mock(Query.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetListOfObjects() {
        new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .build()
                .get()
                .listOfObjects(Object.class)
                .withGetResolver(mock(GetResolver.class))
                .withMapFunc(mock(MapFunc.class))
                .withQuery(mock(Query.class))
                .prepare();
    }
}
