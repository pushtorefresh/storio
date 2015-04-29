package com.pushtorefresh.storio.sqlite.processor.generate;

import com.pushtorefresh.storio.sqlite.annotation.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotation.StorIOSQLiteType;
import com.pushtorefresh.storio.sqlite.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
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

        assertEquals("package com.test;\n" +
                "\n" +
                "import android.support.annotation.NonNull;\n" +
                "import com.pushtorefresh.storio.sqlite.operation.delete.DefaultDeleteResolver;\n" +
                "import com.pushtorefresh.storio.sqlite.query.DeleteQuery;\n" +
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
                "    protected DeleteQuery mapToDeleteQuery(@NonNull TestItem object) {\n" +
                "        return new DeleteQuery.Builder()\n" +
                "            .table(\"test_table\")\n" +
                "            .where(null)\n" +
                "            .whereArgs(null)\n" +
                "            .build();\n" +
                "    }\n" +
                "}\n", out.toString());
    }
}
