package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;

import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryGeneratorTest {

    @Test
    public void createWhereNullStorIOSQLiteTypeMeta() {
        try {
            //noinspection ConstantConditions
            QueryGenerator.createWhere(null, "object");
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasNoCause();
        }
    }

    @Test
    public void createWhereNoColumns() {
        //noinspection ConstantConditions
        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(null, null, null, false);
        assertThat(QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object")).isEqualTo(Collections.emptyMap());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void createWhereNoKeyColumns() {
        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(null, null, null, false);

        final StorIOSQLiteColumn storIOSQLiteColumn1 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn1.key()).thenReturn(false);
        storIOSQLiteTypeMeta.columns.put("column1", new StorIOSQLiteColumnMeta(null, null, null, null, storIOSQLiteColumn1));

        final StorIOSQLiteColumn storIOSQLiteColumn2 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn2.key()).thenReturn(false);
        storIOSQLiteTypeMeta.columns.put("column2", new StorIOSQLiteColumnMeta(null, null, null, null, storIOSQLiteColumn2));

        assertThat(QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object")).isEqualTo(Collections.emptyMap());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void createWhereOneKeyColumn() {
        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(null, null, null, false);

        final Element element = mock(Element.class);
        when(element.getKind()).thenReturn(ElementKind.FIELD);

        final StorIOSQLiteColumn storIOSQLiteColumn1 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn1.key()).thenReturn(false);
        storIOSQLiteTypeMeta.columns.put("column1", new StorIOSQLiteColumnMeta(null, element, null, null, storIOSQLiteColumn1));

        final StorIOSQLiteColumn storIOSQLiteColumn2 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn2.key()).thenReturn(true);
        when(storIOSQLiteColumn2.name()).thenReturn("column2");
        storIOSQLiteTypeMeta.columns.put("column2", new StorIOSQLiteColumnMeta(null, element, "testField1", null, storIOSQLiteColumn2));

        final StorIOSQLiteColumn storIOSQLiteColumn3 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn3.key()).thenReturn(false);
        storIOSQLiteTypeMeta.columns.put("column3", new StorIOSQLiteColumnMeta(null, element, null, null, storIOSQLiteColumn3));

        final Map<String, String> where = QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object");

        assertThat(where.get(QueryGenerator.WHERE_CLAUSE)).isEqualTo("column2 = ?");
        assertThat(where.get(QueryGenerator.WHERE_ARGS)).isEqualTo("object.testField1");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void createWhereMultipleKeyColumns() {
        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(null, null, null, false);

        final Element element = mock(Element.class);
        when(element.getKind()).thenReturn(ElementKind.FIELD);

        final StorIOSQLiteColumn storIOSQLiteColumn1 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn1.key()).thenReturn(true);
        when(storIOSQLiteColumn1.name()).thenReturn("column1");
        storIOSQLiteTypeMeta.columns.put("column1", new StorIOSQLiteColumnMeta(null, element, "testField1", null, storIOSQLiteColumn1));

        final StorIOSQLiteColumn storIOSQLiteColumn2 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn2.key()).thenReturn(false);
        storIOSQLiteTypeMeta.columns.put("column2", new StorIOSQLiteColumnMeta(null, element, null, null, storIOSQLiteColumn2));

        final StorIOSQLiteColumn storIOSQLiteColumn3 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn3.key()).thenReturn(true);
        when(storIOSQLiteColumn3.name()).thenReturn("column3");
        storIOSQLiteTypeMeta.columns.put("column3", new StorIOSQLiteColumnMeta(null, element, "testField3", null, storIOSQLiteColumn3));

        final Map<String, String> where = QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object");

        assertThat(where.get(QueryGenerator.WHERE_CLAUSE)).isEqualTo("column1 = ? AND column3 = ?");
        assertThat(where.get(QueryGenerator.WHERE_ARGS)).isEqualTo("object.testField1, object.testField3");
    }
}
