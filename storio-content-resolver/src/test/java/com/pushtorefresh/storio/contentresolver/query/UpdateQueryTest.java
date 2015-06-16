package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class UpdateQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriObject() {
        UpdateQuery.builder()
                .uri((Uri) null)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriString() {
        UpdateQuery.builder()
                .uri((String) null)
                .build();
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(updateQuery.where());
        assertEquals("", updateQuery.where());
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(updateQuery.whereArgs());
        assertTrue(updateQuery.whereArgs().isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfWhereArgsSpecifiedWithoutWhereClause() {
        UpdateQuery.builder()
                .uri(mock(Uri.class))
                .whereArgs("someArg") // Without WHERE clause!
                .build();
    }

    @Test
    public void buildWithNormalValues() {
        final Uri uri = mock(Uri.class);
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final UpdateQuery updateQuery = UpdateQuery.builder()
                .uri(uri)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        assertEquals(uri, updateQuery.uri());
        assertEquals(where, updateQuery.where());
        assertEquals(asList(whereArgs), updateQuery.whereArgs());
    }
}
