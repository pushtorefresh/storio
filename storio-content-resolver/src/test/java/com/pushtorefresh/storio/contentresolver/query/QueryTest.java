package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class QueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriObject() {
        new Query.Builder()
                .uri((Uri) null)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriString() {
        new Query.Builder()
                .uri((String) null)
                .build();
    }

    @Test
    public void columnsShouldNotBeNull() {
        Query query = new Query.Builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(query.columns());
        assertTrue(query.columns().isEmpty());
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        Query query = new Query.Builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(query.where());
        assertEquals("", query.where());
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        Query query = new Query.Builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(query.whereArgs());
        assertTrue(query.whereArgs().isEmpty());
    }

    @Test
    public void sortOrderShouldNotBeNull() {
        Query query = new Query.Builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(query.sortOrder());
        assertEquals("", query.sortOrder());
    }

    @Test
    public void buildWithNormalValues() {
        final Uri uri = mock(Uri.class);
        final String[] columns = {"1", "2", "3"};
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};
        final String sortOrder = "test_order";

        final Query query = new Query.Builder()
                .uri(uri)
                .columns(columns)
                .where(where)
                .whereArgs(whereArgs)
                .sortOrder(sortOrder)
                .build();

        assertEquals(uri, query.uri());
        assertEquals(asList(columns), query.columns());
        assertEquals(where, query.where());
        assertEquals(asList(whereArgs), query.whereArgs());
        assertEquals(sortOrder, query.sortOrder());
    }
}
