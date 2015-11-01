package com.pushtorefresh.storio.contentresolver.annotations.processor.generate;

import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverColumn;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverType;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PutResolverGeneratorTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void generateJavaFile() throws IOException {
        final PutResolverGenerator putResolverGenerator = new PutResolverGenerator();

        final StorIOContentResolverType storIOContentResolverType = mock(StorIOContentResolverType.class);

        when(storIOContentResolverType.uri()).thenReturn("content://test");

        final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = new StorIOContentResolverTypeMeta("TestItem", "com.test", storIOContentResolverType);

        final StorIOContentResolverColumn storIOContentResolverColumn1 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn1.name()).thenReturn("column1");
        when(storIOContentResolverColumn1.key()).thenReturn(true);
        final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta1 = new StorIOContentResolverColumnMeta(
                null,
                null,
                "column1Field",
                null,
                storIOContentResolverColumn1
        );
        storIOContentResolverTypeMeta.columns.put("column1", storIOContentResolverColumnMeta1);

        final StorIOContentResolverColumn storIOContentResolverColumn2 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn2.name()).thenReturn("column2");
        when(storIOContentResolverColumn2.key()).thenReturn(false);
        final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta2 = new StorIOContentResolverColumnMeta(
                null,
                null,
                "column2Field",
                null,
                storIOContentResolverColumn2
        );
        storIOContentResolverTypeMeta.columns.put("column2", storIOContentResolverColumnMeta2);

        final JavaFile javaFile = putResolverGenerator.generateJavaFile(storIOContentResolverTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        assertThat(out.toString()).isEqualTo("package com.test;\n" +
                "\n" +
                "import android.content.ContentValues;\n" +
                "import android.support.annotation.NonNull;\n" +
                "import com.pushtorefresh.storio.contentresolver.operations.put.DefaultPutResolver;\n" +
                "import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;\n" +
                "import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "/**\n" +
                " * Generated resolver for Put Operation\n" +
                " */\n" +
                "public class TestItemStorIOContentResolverPutResolver extends DefaultPutResolver<TestItem> {\n" +
                "    /**\n" +
                "     * {@inheritDoc}\n" +
                "     */\n" +
                "    @Override\n" +
                "    @NonNull\n" +
                "    protected InsertQuery mapToInsertQuery(@NonNull TestItem object) {\n" +
                "        return InsertQuery.builder()\n" +
                "            .uri(\"content://test\")\n" +
                "            .build();\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * {@inheritDoc}\n" +
                "     */\n" +
                "    @Override\n" +
                "    @NonNull\n" +
                "    protected UpdateQuery mapToUpdateQuery(@NonNull TestItem object) {\n" +
                "        return UpdateQuery.builder()\n" +
                "            .uri(\"content://test\")\n" +
                "            .where(\"column1 = ?\")\n" +
                "            .whereArgs(object.column1Field)\n" +
                "            .build();\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * {@inheritDoc}\n" +
                "     */\n" +
                "    @Override\n" +
                "    @NonNull\n" +
                "    public ContentValues mapToContentValues(@NonNull TestItem object) {\n" +
                "        ContentValues contentValues = new ContentValues(2);\n" +
                "\n" +
                "        contentValues.put(\"column1\", object.column1Field);\n" +
                "        contentValues.put(\"column2\", object.column2Field);\n" +
                "\n" +
                "        return contentValues;\n" +
                "    }\n" +
                "}\n");
    }
}
