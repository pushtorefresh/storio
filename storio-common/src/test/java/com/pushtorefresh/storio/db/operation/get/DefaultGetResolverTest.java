package com.pushtorefresh.storio.db.operation.get;

import android.database.Cursor;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;

import org.junit.Test;

import static junit.framework.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultGetResolverTest {

    @Test
    public void rawQuery() {
        final StorIODb storIODb = mock(StorIODb.class);
        final StorIODb.Internal internal = mock(StorIODb.Internal.class);
        final RawQuery rawQuery = mock(RawQuery.class);
        final Cursor expectedCursor = mock(Cursor.class);

        when(storIODb.internal())
                .thenReturn(internal);

        when(internal.rawQuery(rawQuery))
                .thenReturn(expectedCursor);

        final DefaultGetResolver defaultGetResolver = new DefaultGetResolver();

        final Cursor actualCursor = defaultGetResolver.performGet(storIODb, rawQuery);

        // only one request should occur
        verify(internal, times(1)).rawQuery(any(RawQuery.class));

        // and this request should be equals to original
        verify(internal, times(1)).rawQuery(rawQuery);

        assertSame(expectedCursor, actualCursor);
    }

    @Test
    public void query() {
        final StorIODb storIODb = mock(StorIODb.class);
        final StorIODb.Internal internal = mock(StorIODb.Internal.class);
        final Query query = mock(Query.class);
        final Cursor expectedCursor = mock(Cursor.class);

        when(storIODb.internal())
                .thenReturn(internal);

        when(internal.query(query))
                .thenReturn(expectedCursor);

        final DefaultGetResolver defaultGetResolver = new DefaultGetResolver();

        final Cursor actualCursor = defaultGetResolver.performGet(storIODb, query);

        // only one request should occur
        verify(internal, times(1)).query(any(Query.class));

        // and this request should be equals to original
        verify(internal, times(1)).query(query);

        assertSame(expectedCursor, actualCursor);
    }
}
