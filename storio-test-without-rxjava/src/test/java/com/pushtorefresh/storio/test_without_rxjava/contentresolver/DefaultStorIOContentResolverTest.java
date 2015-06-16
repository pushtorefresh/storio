package com.pushtorefresh.storio.test_without_rxjava.contentresolver;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.query.Query;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DefaultStorIOContentResolverTest {

    @Test
    public void instantiateWithoutRxJava() {
        // Should not fail
        DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetCursor() {
        DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(mock(GetResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetListOfObjects() {
        DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .get()
                .listOfObjects(Object.class)
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withGetResolver(mock(GetResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutObject() {
        DefaultStorIOContentResolver.builder()
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
        DefaultStorIOContentResolver.builder()
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
        DefaultStorIOContentResolver.builder()
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
        DefaultStorIOContentResolver.builder()
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
        DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .delete()
                .byQuery(DeleteQuery.builder()
                        .uri(mock(Uri.class))
                        .build())
                .withDeleteResolver(mock(DeleteResolver.class))
                .prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateDeleteObject() {
        DefaultStorIOContentResolver.builder()
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
        DefaultStorIOContentResolver.builder()
                .contentResolver(mock(ContentResolver.class))
                .build()
                .delete()
                .objects(Object.class, mock(Iterable.class))
                .withDeleteResolver(mock(DeleteResolver.class))
                .prepare();
    }
}
