package com.pushtorefresh.storio.sqlite.operations.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
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
        final DeleteQuery deleteQuery = DeleteQuery.builder()
                .table(testTable)
                .build();

        when(storIOSQLite.lowLevel())
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

        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);
        assertThat(deleteResult.affectedTables()).isEqualTo(Collections.singleton(testTable));
    }

    private static class TestItem {

    }
}
