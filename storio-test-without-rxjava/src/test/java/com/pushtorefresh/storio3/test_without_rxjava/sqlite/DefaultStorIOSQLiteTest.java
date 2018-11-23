package com.pushtorefresh.storio3.test_without_rxjava.sqlite;

import android.content.ContentValues;

import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio3.sqlite.queries.Query;

import org.junit.Test;

import java.util.Collection;

import androidx.sqlite.db.SupportSQLiteOpenHelper;

import static org.mockito.Mockito.mock;

public class DefaultStorIOSQLiteTest {

    @Test
    public void instantiateWithoutRxJava() {
        // Should not fail
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SupportSQLiteOpenHelper.class))
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetCursor() {
        DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mock(SupportSQLiteOpenHelper.class))
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
                .sqliteOpenHelper(mock(SupportSQLiteOpenHelper.class))
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
                .sqliteOpenHelper(mock(SupportSQLiteOpenHelper.class))
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
                .sqliteOpenHelper(mock(SupportSQLiteOpenHelper.class))
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
                .sqliteOpenHelper(mock(SupportSQLiteOpenHelper.class))
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
                .sqliteOpenHelper(mock(SupportSQLiteOpenHelper.class))
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
                .sqliteOpenHelper(mock(SupportSQLiteOpenHelper.class))
                .build()
                .put()
                .contentValues(mock(ContentValues.class), mock(ContentValues.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }
}
