package com.pushtorefresh.storio.contentresolver.annotations.processor.generate;

import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverColumn;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverType;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetResolverGeneratorTest {

    @Test
    public void generateJavaFileTest() throws IOException {
        final StorIOContentResolverType storIOContentResolverType = mock(StorIOContentResolverType.class);

        when(storIOContentResolverType.uri()).thenReturn("content://test");

        final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = new StorIOContentResolverTypeMeta(
                "TestItem",
                "com.test",
                storIOContentResolverType
        );

        final StorIOContentResolverColumn storIOContentResolverColumn1 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn1.name()).thenReturn("column1");

        //noinspection ConstantConditions
        final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta1 = new StorIOContentResolverColumnMeta(
                null,
                null,
                "field1",
                JavaType.BOOLEAN,
                storIOContentResolverColumn1
        );
        storIOContentResolverTypeMeta.columns.put("column1", storIOContentResolverColumnMeta1);

        final StorIOContentResolverColumn storIOContentResolverColumn2 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn2.name()).thenReturn("column2");

        //noinspection ConstantConditions
        final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta2 = new StorIOContentResolverColumnMeta(
                null,
                null,
                "field2",
                JavaType.STRING,
                storIOContentResolverColumn2
        );
        storIOContentResolverTypeMeta.columns.put("column2", storIOContentResolverColumnMeta2);

        final JavaFile javaFile = new GetResolverGenerator().generateJavaFile(storIOContentResolverTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        assertThat(out.toString()).isEqualTo("package com.test;\n" +
                "\n" +
                "import android.database.Cursor;\n" +
                "import android.support.annotation.NonNull;\n" +
                "import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "/**\n" +
                " * Generated resolver for Get Operation\n" +
                " */\n" +
                "public class TestItemStorIOContentResolverGetResolver extends DefaultGetResolver<TestItem> {\n" +
                "    /**\n" +
                "     * {@inheritDoc}\n" +
                "     */\n" +
                "    @Override\n" +
                "    @NonNull\n" +
                "    public TestItem mapFromCursor(@NonNull Cursor cursor) {\n" +
                "        TestItem object = new TestItem();\n" +
                "\n" +
                "        object.field1 = cursor.getInt(cursor.getColumnIndex(\"column1\")) == 1;\n" +
                "        object.field2 = cursor.getString(cursor.getColumnIndex(\"column2\"));\n" +
                "\n" +
                "        return object;\n" +
                "    }\n" +
                "}\n");
    }
}
