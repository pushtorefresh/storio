package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DeleteQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullUri() {
        new DeleteQuery.Builder()
                .uri((Uri) null) // LOL, via overload we disable null uri without specifying Type!
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = RuntimeException.class) // Uri#parse() not mocked
    public void nullUriString() {
        new DeleteQuery.Builder()
                .uri((String) null)
                .build();
    }

    @Test
    public void build() {
        final Uri uri = mock(Uri.class);
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final DeleteQuery deleteQuery = new DeleteQuery.Builder()
                .uri(uri)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        assertEquals(uri, deleteQuery.uri);
        assertEquals(where, deleteQuery.where);
        assertEquals(Arrays.asList(whereArgs), deleteQuery.whereArgs);
    }
}
