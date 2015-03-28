package com.pushtorefresh.storio.db.operation.delete;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.query.DeleteQuery;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultDeleteResolverTest {

    @Test public void performDelete() {
        final StorIODb storIODb = mock(StorIODb.class);
        final StorIODb.Internal internal = mock(StorIODb.Internal.class);
        final DeleteQuery deleteQuery = mock(DeleteQuery.class);

        when(storIODb.internal())
                .thenReturn(internal);

        when(internal.delete(deleteQuery))
                .thenReturn(1);

        final DefaultDeleteResolver defaultDeleteResolver = new DefaultDeleteResolver();
        final int result = defaultDeleteResolver.performDelete(storIODb, deleteQuery);

        verify(internal, times(1)).delete(any(DeleteQuery.class));
        verify(internal, times(1)).delete(deleteQuery);

        assertEquals(1, result);
    }
}
