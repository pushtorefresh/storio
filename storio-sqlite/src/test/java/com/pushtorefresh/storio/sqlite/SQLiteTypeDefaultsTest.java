package com.pushtorefresh.storio.sqlite;

import com.pushtorefresh.storio.sqlite.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operation.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operation.put.PutResolver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SQLiteTypeDefaultsTest {

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullPutResolver() {
        new SQLiteTypeDefaults.Builder<Object>()
                .putResolver(null)
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullMapFromCursor() {
        new SQLiteTypeDefaults.Builder<Object>()
                .putResolver(mock(PutResolver.class))
                .getResolver(null)
                .deleteResolver(mock(DeleteResolver.class))
                .build();
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullMapToDeleteQuery() {
        new SQLiteTypeDefaults.Builder<Object>()
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

        final SQLiteTypeDefaults<TestItem> sqliteTypeDefaults = new SQLiteTypeDefaults.Builder<TestItem>()
                .putResolver(putResolver)
                .getResolver(getResolver)
                .deleteResolver(deleteResolver)
                .build();

        assertEquals(putResolver, sqliteTypeDefaults.putResolver);
        assertEquals(getResolver, sqliteTypeDefaults.getResolver);
        assertEquals(deleteResolver, sqliteTypeDefaults.deleteResolver);
    }
}
