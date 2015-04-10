package com.pushtorefresh.storio.contentresolver.query;

import android.net.Uri;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DeleteQueryTest {

    @Test(expected = NullPointerException.class)
    public void nullUri() {
        new DeleteQuery.Builder()
                .uri((Uri) null) // LOL, via overload we disable null uri without specifying Type!
                .build();
    }

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
        final Object[] whereArgs = new String[] {
                "arg1",
                "arg2"
        };

        new DeleteQuery.Builder()
                .uri(uri)
                .where(where)
                .whereArgs(whereArgs)
                .build();
    }
}
