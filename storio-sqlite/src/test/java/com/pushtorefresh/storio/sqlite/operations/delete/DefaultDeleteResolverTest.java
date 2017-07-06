package com.pushtorefresh.storio.sqlite.operations.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

import org.junit.Test;

import java.util.Set;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultDeleteResolverTest {

    @Test
    public void performDelete() {
        final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = mock(StorIOSQLite.LowLevel.class);

        final String testTable = "test_table";
        final Set<String> testTags = singleton("test_tag");
        final DeleteQuery deleteQuery = DeleteQuery.builder()
                .table(testTable)
                .affectsTags(testTags)
                .build();

        when(storIOSQLite.lowLevel())
                .thenReturn(lowLevel);

        when(lowLevel.delete(deleteQuery))
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

        verify(lowLevel).delete(any(DeleteQuery.class));
        verify(lowLevel).delete(deleteQuery);

        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);
        assertThat(deleteResult.affectedTables()).isEqualTo(singleton(testTable));
        assertThat(deleteResult.affectedTags()).isEqualTo(testTags);
    }

    private static class TestItem {

    }
}
