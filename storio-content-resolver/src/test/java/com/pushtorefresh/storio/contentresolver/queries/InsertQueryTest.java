package com.pushtorefresh.storio.contentresolver.queries;

import android.net.Uri;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class InsertQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriObject() {
        InsertQuery.builder()
                .uri((Uri) null)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriString() {
        InsertQuery.builder()
                .uri((String) null)
                .build();
    }

    @Test
    public void buildWithNormalValues() {
        final Uri uri = mock(Uri.class);

        final InsertQuery insertQuery = InsertQuery.builder()
                .uri(uri)
                .build();

        assertEquals(uri, insertQuery.uri());
    }
}
