package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

import org.junit.Test;

import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultDeleteResolverTest {

    @Test
    public void performDelete() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

        final String testTable = "test_table";
        final DeleteQuery deleteQuery = new DeleteQuery.Builder()
                .table(testTable)
                .build();

        when(storIOSQLite.internal())
                .thenReturn(internal);

        when(internal.delete(deleteQuery))
                .thenReturn(1);

        final TestItem testItem = new TestItem();

        final DefaultDeleteResolver<TestItem> defaultDeleteResolver = new DefaultDeleteResolver<TestItem>() {
            @NonNull
            @Override
            public DeleteQuery mapToDeleteQuery(@NonNull TestItem testItem) {
                return deleteQuery;
            }
        };

        final DeleteResult deleteResult = defaultDeleteResolver.performDelete(storIOSQLite, testItem);

        verify(internal, times(1)).delete(any(DeleteQuery.class));
        verify(internal, times(1)).delete(deleteQuery);

        assertEquals(1, deleteResult.numberOfRowsDeleted());
        assertEquals(Collections.singleton(testTable), deleteResult.affectedTables());
    }

    private static class TestItem {

    }
}
