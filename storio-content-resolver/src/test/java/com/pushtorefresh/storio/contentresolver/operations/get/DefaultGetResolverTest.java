package com.pushtorefresh.storio.contentresolver.operations.get;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultGetResolverTest {

    @Test
    public void query() {
        final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
        final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

        final Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        final Cursor expectedCursor = mock(Cursor.class);

        when(storIOContentResolver.lowLevel())
                .thenReturn(lowLevel);

        when(lowLevel.query(query))
                .thenReturn(expectedCursor);

        final GetResolver<TestItem> defaultGetResolver = new DefaultGetResolver<TestItem>() {
            @NonNull
            @Override
            public TestItem mapFromCursor(@NonNull Cursor cursor) {
                assertThat(cursor).isSameAs(expectedCursor);
                return new TestItem();
            }
        };

        final Cursor actualCursor = defaultGetResolver.performGet(storIOContentResolver, query);

        // only one request should occur
        verify(lowLevel, times(1)).query(any(Query.class));

        // and this request should be equals to original
        verify(lowLevel, times(1)).query(query);

        assertThat(actualCursor).isSameAs(expectedCursor);
    }

    private static class TestItem {

    }
}
