package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class UpdateQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullUri() {
        new UpdateQuery.Builder()
                .uri((Uri) null)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = RuntimeException.class)
    public void nullUriString() {
        new UpdateQuery.Builder()
                .uri((String) null)
                .build();
    }

    @Test
    public void build() {
        final Uri uri = mock(Uri.class);
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final UpdateQuery updateQuery = new UpdateQuery.Builder()
                .uri(uri)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        assertEquals(uri, updateQuery.uri);
        assertEquals(where, updateQuery.where);
        assertEquals(Arrays.asList(whereArgs), updateQuery.whereArgs);
    }
}
