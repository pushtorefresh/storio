package com.pushtorefresh.storio.contentresolver.annotations.processor.generate;

import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverColumn;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;

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
    public void createWhereNullStorIOContentResolverTypeMeta() {
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
        final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = new StorIOContentResolverTypeMeta(null, null, null);
        assertThat(QueryGenerator.createWhere(storIOContentResolverTypeMeta, "object")).isEqualTo(Collections.emptyMap());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void createWhereNoKeyColumns() {
        final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = new StorIOContentResolverTypeMeta(null, null, null);

        final StorIOContentResolverColumn storIOContentResolverColumn1 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn1.key()).thenReturn(false);
        storIOContentResolverTypeMeta.columns.put("column1", new StorIOContentResolverColumnMeta(null, null, null, null, storIOContentResolverColumn1));

        final StorIOContentResolverColumn storIOContentResolverColumn2 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn2.key()).thenReturn(false);
        storIOContentResolverTypeMeta.columns.put("column2", new StorIOContentResolverColumnMeta(null, null, null, null, storIOContentResolverColumn2));

        assertThat(QueryGenerator.createWhere(storIOContentResolverTypeMeta, "object")).isEqualTo(Collections.emptyMap());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void createWhereOneKeyColumn() {
        final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = new StorIOContentResolverTypeMeta(null, null, null);

        final Element element = mock(Element.class);
        when(element.getKind()).thenReturn(ElementKind.FIELD);

        final StorIOContentResolverColumn storIOContentResolverColumn1 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn1.key()).thenReturn(false);
        storIOContentResolverTypeMeta.columns.put("column1", new StorIOContentResolverColumnMeta(null, element, null, null, storIOContentResolverColumn1));

        final StorIOContentResolverColumn storIOContentResolverColumn2 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn2.key()).thenReturn(true);
        when(storIOContentResolverColumn2.name()).thenReturn("column2");
        storIOContentResolverTypeMeta.columns.put("column2", new StorIOContentResolverColumnMeta(null, element, "testField1", null, storIOContentResolverColumn2));

        final StorIOContentResolverColumn storIOContentResolverColumn3 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn3.key()).thenReturn(false);
        storIOContentResolverTypeMeta.columns.put("column3", new StorIOContentResolverColumnMeta(null, element, null, null, storIOContentResolverColumn3));

        final Map<String, String> where = QueryGenerator.createWhere(storIOContentResolverTypeMeta, "object");

        assertThat(where.get(QueryGenerator.WHERE_CLAUSE)).isEqualTo("column2 = ?");
        assertThat(where.get(QueryGenerator.WHERE_ARGS)).isEqualTo("object.testField1");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void createWhereMultipleKeyColumns() {
        final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = new StorIOContentResolverTypeMeta(null, null, null);

        final Element element = mock(Element.class);
        when(element.getKind()).thenReturn(ElementKind.FIELD);

        final StorIOContentResolverColumn storIOContentResolverColumn1 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn1.key()).thenReturn(true);
        when(storIOContentResolverColumn1.name()).thenReturn("column1");
        storIOContentResolverTypeMeta.columns.put("column1", new StorIOContentResolverColumnMeta(null, element, "testField1", null, storIOContentResolverColumn1));

        final StorIOContentResolverColumn storIOContentResolverColumn2 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn2.key()).thenReturn(false);
        storIOContentResolverTypeMeta.columns.put("column2", new StorIOContentResolverColumnMeta(null, element, null, null, storIOContentResolverColumn2));

        final StorIOContentResolverColumn storIOContentResolverColumn3 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn3.key()).thenReturn(true);
        when(storIOContentResolverColumn3.name()).thenReturn("column3");
        storIOContentResolverTypeMeta.columns.put("column3", new StorIOContentResolverColumnMeta(null, element, "testField3", null, storIOContentResolverColumn3));

        final Map<String, String> where = QueryGenerator.createWhere(storIOContentResolverTypeMeta, "object");

        assertThat(where.get(QueryGenerator.WHERE_CLAUSE)).isEqualTo("column1 = ? AND column3 = ?");
        assertThat(where.get(QueryGenerator.WHERE_ARGS)).isEqualTo("object.testField1, object.testField3");
    }
}
