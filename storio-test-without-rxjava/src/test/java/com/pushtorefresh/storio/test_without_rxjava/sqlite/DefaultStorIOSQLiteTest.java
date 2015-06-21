package com.pushtorefresh.storio.test_without_rxjava.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio.sqlite.queries.Query;

import org.junit.Test;

import java.util.Collection;

import static org.mockito.Mockito.mock;

public class DefaultStorIOSQLiteTest {

    @Test
    public void instantiateWithoutRxJava() {
        // Should not fail
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetCursor() {
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build()
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .table("test_table")
                        .build())
                .withGetResolver(mock(GetResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetListOfObjects() {
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build()
                .get()
                .listOfObjects(Object.class)
                .withQuery(Query.builder()
                        .table("test_table")
                        .build())
                .withGetResolver(mock(GetResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutObject() {
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build()
                .put()
                .object(mock(Object.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutCollectionOfObjects() {
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build()
                .put()
                .objects(mock(Collection.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValues() {
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build()
                .put()
                .contentValues(mock(ContentValues.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValuesIterable() {
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build()
                .put()
                .contentValues(mock(Iterable.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValuesVarArgs() {
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SQLiteOpenHelper.class))
                .build()
                .put()
                .contentValues(mock(ContentValues.class), mock(ContentValues.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }
}
