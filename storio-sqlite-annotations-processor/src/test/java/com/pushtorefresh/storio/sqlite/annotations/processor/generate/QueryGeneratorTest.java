package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;

import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryGeneratorTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void createWhereNullStorIOSQLiteTypeMeta() {
        QueryGenerator.createWhere(null, "object");
    }

    @Test
    public void createWhereNoColumns() {
        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(null, null, null);
        assertEquals(Collections.emptyMap(), QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object"));
    }

    @Test
    public void createWhereNoKeyColumns() {
        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(null, null, null);

        final StorIOSQLiteColumn storIOSQLiteColumn1 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn1.key()).thenReturn(false);
        storIOSQLiteTypeMeta.columns.put("column1", new StorIOSQLiteColumnMeta(null, null, null, null, storIOSQLiteColumn1));

        final StorIOSQLiteColumn storIOSQLiteColumn2 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn2.key()).thenReturn(false);
        storIOSQLiteTypeMeta.columns.put("column2", new StorIOSQLiteColumnMeta(null, null, null, null, storIOSQLiteColumn2));

        assertEquals(Collections.emptyMap(), QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object"));
    }

    @Test
    public void createWhereOneKeyColumn() {
        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(null, null, null);

        final StorIOSQLiteColumn storIOSQLiteColumn1 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn1.key()).thenReturn(false);
        storIOSQLiteTypeMeta.columns.put("column1", new StorIOSQLiteColumnMeta(null, null, null, null, storIOSQLiteColumn1));

        final StorIOSQLiteColumn storIOSQLiteColumn2 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn2.key()).thenReturn(true);
        when(storIOSQLiteColumn2.name()).thenReturn("column2");
        storIOSQLiteTypeMeta.columns.put("column2", new StorIOSQLiteColumnMeta(null, null, "testField1", null, storIOSQLiteColumn2));

        final StorIOSQLiteColumn storIOSQLiteColumn3 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn3.key()).thenReturn(false);
        storIOSQLiteTypeMeta.columns.put("column3", new StorIOSQLiteColumnMeta(null, null, null, null, storIOSQLiteColumn3));

        final Map<String, String> where = QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object");

        assertEquals("column2 = ?", where.get(QueryGenerator.WHERE_CLAUSE));
        assertEquals("object.testField1", where.get(QueryGenerator.WHERE_ARGS));
    }

    @Test
    public void createWhereMultipleKeyColumns() {
        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(null, null, null);

        final StorIOSQLiteColumn storIOSQLiteColumn1 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn1.key()).thenReturn(true);
        when(storIOSQLiteColumn1.name()).thenReturn("column1");
        storIOSQLiteTypeMeta.columns.put("column1", new StorIOSQLiteColumnMeta(null, null, "testField1", null, storIOSQLiteColumn1));

        final StorIOSQLiteColumn storIOSQLiteColumn2 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn2.key()).thenReturn(false);
        storIOSQLiteTypeMeta.columns.put("column2", new StorIOSQLiteColumnMeta(null, null, null, null, storIOSQLiteColumn2));

        final StorIOSQLiteColumn storIOSQLiteColumn3 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn3.key()).thenReturn(true);
        when(storIOSQLiteColumn3.name()).thenReturn("column3");
        storIOSQLiteTypeMeta.columns.put("column3", new StorIOSQLiteColumnMeta(null, null, "testField3", null, storIOSQLiteColumn3));

        final Map<String, String> where = QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object");

        assertEquals("column1 = ? AND column3 = ?", where.get(QueryGenerator.WHERE_CLAUSE));
        assertEquals("object.testField1, object.testField3", where.get(QueryGenerator.WHERE_ARGS));
    }
}
