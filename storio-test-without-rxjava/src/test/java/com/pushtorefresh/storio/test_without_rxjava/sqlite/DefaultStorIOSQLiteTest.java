package com.pushtorefresh.storio.test_without_rxjava.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operation.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operation.put.PutResolver;
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
                .withQuery(mock(Query.class))
                .withGetResolver(mock(GetResolver.class))
                .withMapFunc(mock(MapFunc.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValues() {
        new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .build()
                .put()
                .contentValues(mock(ContentValues.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValuesIterable() {
        new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .build()
                .put()
                .contentValues(mock(Iterable.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValuesVarArgs() {
        new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .build()
                .put()
                .contentValues(mock(ContentValues.class), mock(ContentValues.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutObject() {
        new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .build()
                .put()
                .object(mock(Object.class))
                .withPutResolver(mock(PutResolver.class))
                .withMapFunc(mock(MapFunc.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutObjectsIterable() {
        new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .build()
                .put()
                .objects(Object.class, mock(Iterable.class))
                .withPutResolver(mock(PutResolver.class))
                .withMapFunc(mock(MapFunc.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutObjectsVarArgs() {
        new DefaultStorIOSQLite.Builder()
                .db(mock(SQLiteDatabase.class))
                .build()
                .put()
                .objects(Object.class, mock(Object.class), mock(Object.class))
                .withPutResolver(mock(PutResolver.class))
                .withMapFunc(mock(MapFunc.class))
                .prepare();
    }
}
