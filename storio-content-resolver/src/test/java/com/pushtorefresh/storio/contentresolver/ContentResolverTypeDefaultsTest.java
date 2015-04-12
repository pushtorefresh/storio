package com.pushtorefresh.storio.contentresolver;

import android.content.ContentValues;
import android.database.Cursor;

import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.operation.MapFunc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ContentResolverTypeDefaultsTest {

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void nullMapToContentValues() {
        new ContentResolverTypeDefaults.Builder<Object>()
                .mappingToContentValues(null)
                .mappingFromCursor(mock(MapFunc.class))
                .putResolver(mock(PutResolver.class))
                .mappingToDeleteQuery(mock(MapFunc.class))
                .build();
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void nullMappingFromCursor() {
        new ContentResolverTypeDefaults.Builder<Object>()
                .mappingToContentValues(mock(MapFunc.class))
                .mappingFromCursor(null)
                .putResolver(mock(PutResolver.class))
                .mappingToDeleteQuery(mock(MapFunc.class))
                .build();
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void nullPutResolver() {
        new ContentResolverTypeDefaults.Builder<Object>()
                .mappingToContentValues(mock(MapFunc.class))
                .mappingFromCursor(mock(MapFunc.class))
                .putResolver(null)
                .mappingToDeleteQuery(mock(MapFunc.class))
                .build();
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void nullMapToDeleteQuery() {
        new ContentResolverTypeDefaults.Builder<Object>()
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

        final ContentResolverTypeDefaults<TestItem> contentResolverTypeDefaults = new ContentResolverTypeDefaults.Builder<TestItem>()
                .mappingToContentValues(mapToContentValues)
                .mappingFromCursor(mapFromCursor)
                .putResolver(putResolver)
                .mappingToDeleteQuery(mapToDeleteQuery)
                .getResolver(getResolver)
                .deleteResolver(deleteResolver)
                .build();

        assertEquals(mapToContentValues, contentResolverTypeDefaults.mapToContentValues);
        assertEquals(mapFromCursor, contentResolverTypeDefaults.mapFromCursor);
        assertEquals(putResolver, contentResolverTypeDefaults.putResolver);
        assertEquals(mapToDeleteQuery, contentResolverTypeDefaults.mapToDeleteQuery);
        assertEquals(getResolver, contentResolverTypeDefaults.getResolver);
        assertEquals(deleteResolver, contentResolverTypeDefaults.deleteResolver);
    }
}
