package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetResolverGeneratorTest {

    @Test
    public void generateJavaFileTest() throws IOException {
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
                JavaType.BOOLEAN,
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
                JavaType.STRING,
                storIOSQLiteColumn2
        );
        storIOSQLiteTypeMeta.columns.put("column2", storIOSQLiteColumnMeta2);

        final JavaFile javaFile = new GetResolverGenerator().generateJavaFile(storIOSQLiteTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        assertThat(out.toString()).isEqualTo("package com.test;\n" +
                "\n" +
                "import android.database.Cursor;\n" +
                "import android.support.annotation.NonNull;\n" +
                "import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "/**\n" +
                " * Generated resolver for Get Operation\n" +
                " */\n" +
                "public class TestItemStorIOSQLiteGetResolver extends DefaultGetResolver<TestItem> {\n" +
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
