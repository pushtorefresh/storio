package com.pushtorefresh.storio.sqlite;

import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SQLiteTypeMappingTest {

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullPutResolver() {
        SQLiteTypeMapping.builder()
                .putResolver(null)
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullMapFromCursor() {
        SQLiteTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(null)
                .deleteResolver(mock(DeleteResolver.class))
                .build();
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullMapToDeleteQuery() {
        SQLiteTypeMapping.builder()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(null)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void build() {
        class TestItem {

        }

        final PutResolver<TestItem> putResolver = mock(PutResolver.class);
        final GetResolver<TestItem> getResolver = mock(GetResolver.class);
        final DeleteResolver<TestItem> deleteResolver = mock(DeleteResolver.class);

        final SQLiteTypeMapping<TestItem> typeMapping = SQLiteTypeMapping.<TestItem>builder()
                .putResolver(putResolver)
                .getResolver(getResolver)
                .deleteResolver(deleteResolver)
                .build();

        assertThat(typeMapping.putResolver()).isSameAs(putResolver);
        assertThat(typeMapping.getResolver()).isSameAs(getResolver);
        assertThat(typeMapping.deleteResolver()).isSameAs(deleteResolver);
    }
}
