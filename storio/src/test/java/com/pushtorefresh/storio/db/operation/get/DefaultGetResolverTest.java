package com.pushtorefresh.storio.db.operation.get;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultGetResolverTest {

    @Test public void rawQuery() {
        final StorIODb storIODb = mock(StorIODb.class);
        final StorIODb.Internal internal = mock(StorIODb.Internal.class);

        when(storIODb.internal())
                .thenReturn(internal);

        final DefaultGetResolver defaultGetResolver = new DefaultGetResolver();
        final RawQuery rawQuery = mock(RawQuery.class);

        defaultGetResolver.performGet(storIODb, rawQuery);

        // only one request should occur
        verify(internal, times(1)).rawQuery(any(RawQuery.class));

        // and this request should be equals to original
        verify(internal, times(1)).rawQuery(rawQuery);
    }

    @Test public void query() {
        final StorIODb storIODb = mock(StorIODb.class);
        final StorIODb.Internal internal = mock(StorIODb.Internal.class);

        when(storIODb.internal())
                .thenReturn(internal);

        final DefaultGetResolver defaultGetResolver = new DefaultGetResolver();
        final Query query = mock(Query.class);

        defaultGetResolver.performGet(storIODb, query);

        // only one request should occur
        verify(internal, times(1)).query(any(Query.class));

        // and this request should be equals to original
        verify(internal, times(1)).query(query);
    }
}
