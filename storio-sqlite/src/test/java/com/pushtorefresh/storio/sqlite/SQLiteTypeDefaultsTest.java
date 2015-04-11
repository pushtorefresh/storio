package com.pushtorefresh.storio.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlite.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operation.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operation.put.PutResolver;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SQLiteTypeDefaultsTest {

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullMapToContentValues() {
        new SQLiteTypeDefaults.Builder<Object>()
                .mappingToContentValues(null)
                .mappingFromCursor(mock(MapFunc.class))
                .putResolver(mock(PutResolver.class))
                .mappingToDeleteQuery(mock(MapFunc.class))
                .build();
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullMapFromCursor() {
        new SQLiteTypeDefaults.Builder<Object>()
                .mappingToContentValues(mock(MapFunc.class))
                .mappingFromCursor(null)
                .putResolver(mock(PutResolver.class))
                .mappingToDeleteQuery(mock(MapFunc.class))
                .build();
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullMapToDeleteQuery() {
        new SQLiteTypeDefaults.Builder<Object>()
                .mappingToContentValues(mock(MapFunc.class))
                .mappingFromCursor(mock(MapFunc.class))
                .putResolver(mock(PutResolver.class))
                .mappingToDeleteQuery(null)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void build() {
        class TestItem {

        }

        final MapFunc<TestItem, ContentValues> mapToContentValues = mock(MapFunc.class);
        final MapFunc<Cursor, TestItem> mapFromCursor = mock(MapFunc.class);
        final PutResolver<TestItem> putResolver = mock(PutResolver.class);
        final MapFunc<TestItem, DeleteQuery> mapToDeleteQuery = mock(MapFunc.class);
        final GetResolver getResolver = mock(GetResolver.class);
        final DeleteResolver deleteResolver = mock(DeleteResolver.class);

        final SQLiteTypeDefaults<TestItem> sqliteTypeDefaults = new SQLiteTypeDefaults.Builder<TestItem>()
                .mappingToContentValues(mapToContentValues)
                .mappingFromCursor(mapFromCursor)
                .putResolver(putResolver)
                .mappingToDeleteQuery(mapToDeleteQuery)
                .getResolver(getResolver)
                .deleteResolver(deleteResolver)
                .build();

        assertEquals(mapToContentValues, sqliteTypeDefaults.mapToContentValues);
        assertEquals(mapFromCursor, sqliteTypeDefaults.mapFromCursor);
        assertEquals(putResolver, sqliteTypeDefaults.putResolver);
        assertEquals(mapToDeleteQuery, sqliteTypeDefaults.mapToDeleteQuery);
        assertEquals(getResolver, sqliteTypeDefaults.getResolver);
        assertEquals(deleteResolver, sqliteTypeDefaults.deleteResolver);
    }
}
