package com.pushtorefresh.storio.test_without_rxjava.contentresolver;

import android.content.ContentResolver;
import android.content.ContentValues;

import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.contentresolver.operation.get.GetResolver;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DefaultStorIOContentResolverTest {

    @Test
    public void instantiateWithoutRxJava() {
        // Should not fail
        new DefaultStorIOContentResolver.Builder()
                .contentResolver(mock(ContentResolver.class))
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetCursor() {
        new DefaultStorIOContentResolver.Builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .get()
                .cursor()
                .withQuery(mock(Query.class))
                .withGetResolver(mock(GetResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetListOfObjects() {
        new DefaultStorIOContentResolver.Builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .get()
                .listOfObjects(Object.class)
                .withQuery(mock(Query.class))
                .withGetResolver(mock(GetResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutObject() {
        new DefaultStorIOContentResolver.Builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .put()
                .object(new Object())
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutObjects() {
        new DefaultStorIOContentResolver.Builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .put()
                .objects(Object.class, mock(Iterable.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValues() {
        new DefaultStorIOContentResolver.Builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .put()
                .contentValues(mock(ContentValues.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValuesIterable() {
        new DefaultStorIOContentResolver.Builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .put()
                .contentValues(mock(Iterable.class))
                .withPutResolver(mock(PutResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateDeleteByQuery() {
        new DefaultStorIOContentResolver.Builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .delete()
                .byQuery(mock(DeleteQuery.class))
                .withDeleteResolver(mock(DeleteResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateDeleteObject() {
        new DefaultStorIOContentResolver.Builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .delete()
                .object(new Object())
                .withDeleteResolver(mock(DeleteResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateDeleteObjects() {
        new DefaultStorIOContentResolver.Builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .delete()
                .objects(Object.class, mock(Iterable.class))
                .withDeleteResolver(mock(DeleteResolver.class))
                .prepare();
    }
}
