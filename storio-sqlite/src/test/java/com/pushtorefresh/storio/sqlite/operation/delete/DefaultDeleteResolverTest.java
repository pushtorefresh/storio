package com.pushtorefresh.storio.sqlite.operation.delete;

import com.pushtorefresh.storio.sqlite.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultDeleteResolverTest {

    @Test public void performDelete() {
        final StorIOSQLiteDb storIOSQLiteDb = mock(StorIOSQLiteDb.class);
        final StorIOSQLiteDb.Internal internal = mock(StorIOSQLiteDb.Internal.class);
        final DeleteQuery deleteQuery = mock(DeleteQuery.class);

        when(storIOSQLiteDb.internal())
                .thenReturn(internal);

        when(internal.delete(deleteQuery))
                .thenReturn(1);

        final DefaultDeleteResolver defaultDeleteResolver = new DefaultDeleteResolver();
        final int result = defaultDeleteResolver.performDelete(storIOSQLiteDb, deleteQuery);

        verify(internal, times(1)).delete(any(DeleteQuery.class));
        verify(internal, times(1)).delete(deleteQuery);

        assertEquals(1, result);
    }
}
