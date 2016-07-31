package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeleteResolverGeneratorTest {

    @Test
    public void generateJavaFile() throws IOException {
        final StorIOSQLiteType storIOSQLiteType = mock(StorIOSQLiteType.class);

        when(storIOSQLiteType.table()).thenReturn("test_table");

        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(
                "TestItem",
                "com.test",
                storIOSQLiteType
        );

        final StorIOSQLiteColumn storIOSQLiteColumn1 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn1.name()).thenReturn("column1");

        //noinspection ConstantConditions
        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta1 = new StorIOSQLiteColumnMeta(
                null,
                null,
                "field1",
                null,
                storIOSQLiteColumn1
        );
        storIOSQLiteTypeMeta.columns.put("column1", storIOSQLiteColumnMeta1);

        final StorIOSQLiteColumn storIOSQLiteColumn2 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn2.name()).thenReturn("column2");

        //noinspection ConstantConditions
        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta2 = new StorIOSQLiteColumnMeta(
                null,
                null,
                "field2",
                null,
                storIOSQLiteColumn2
        );
        storIOSQLiteTypeMeta.columns.put("column2", storIOSQLiteColumnMeta2);

        final JavaFile javaFile = new DeleteResolverGenerator().generateJavaFile(storIOSQLiteTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        assertThat(out.toString()).isEqualTo("package com.test;\n" +
                "\n" +
                "import android.support.annotation.NonNull;\n" +
                "import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver;\n" +
                "import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "/**\n" +
                " * Generated resolver for Delete Operation\n" +
                " */\n" +
                "public class TestItemStorIOSQLiteDeleteResolver extends DefaultDeleteResolver<TestItem> {\n" +
                "    /**\n" +
                "     * {@inheritDoc}\n" +
                "     */\n" +
                "    @Override\n" +
                "    @NonNull\n" +
                "    public DeleteQuery mapToDeleteQuery(@NonNull TestItem object) {\n" +
                "        return DeleteQuery.builder()\n" +
                "            .table(\"test_table\")\n" +
                "            .where(null)\n" +
                "            .whereArgs(null)\n" +
                "            .build();\n" +
                "    }\n" +
                "}\n");
    }
}
