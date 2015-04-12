package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class QueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullUri() {
        new Query.Builder()
                .uri((Uri) null)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = RuntimeException.class)
    public void nullUriString() {
        new Query.Builder()
                .uri((String) null)
                .build();
    }

    @Test
    public void build() {
        final Uri uri = mock(Uri.class);
        final String[] projection = {"1", "2", "3"};
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};
        final String sortOrder = "test_order";

        final Query query = new Query.Builder()
                .uri(uri)
                .projection(projection)
                .where(where)
                .whereArgs(whereArgs)
                .sortOrder(sortOrder)
                .build();

        assertEquals(uri, query.uri);
        assertEquals(Arrays.asList(projection), query.projection);
        assertEquals(where, query.where);
        assertEquals(Arrays.asList(whereArgs), query.whereArgs);
        assertEquals(sortOrder, query.sortOrder);
    }
}
