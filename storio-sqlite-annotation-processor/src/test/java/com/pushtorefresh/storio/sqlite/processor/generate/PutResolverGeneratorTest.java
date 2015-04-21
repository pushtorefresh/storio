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

public class PutResolverGeneratorTest {

    @Test
    public void generateJavaFile() throws IOException {
        final PutResolverGenerator putResolverGenerator = new PutResolverGenerator();

        final StorIOSQLiteType storIOSQLiteType = mock(StorIOSQLiteType.class);

        when(storIOSQLiteType.table()).thenReturn("test_table");

        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta("TestItem", "com.test", storIOSQLiteType);

        final StorIOSQLiteColumn storIOSQLiteColumn1 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn1.name()).thenReturn("column1");
        when(storIOSQLiteColumn1.key()).thenReturn(true);
        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta1 = new StorIOSQLiteColumnMeta(null, "column1Field", null, storIOSQLiteColumn1);
        storIOSQLiteTypeMeta.columns.put("column1", storIOSQLiteColumnMeta1);

        final StorIOSQLiteColumn storIOSQLiteColumn2 = mock(StorIOSQLiteColumn.class);
        when(storIOSQLiteColumn2.name()).thenReturn("column2");
        when(storIOSQLiteColumn2.key()).thenReturn(false);
        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta2 = new StorIOSQLiteColumnMeta(null, "column2Field", null, storIOSQLiteColumn2);
        storIOSQLiteTypeMeta.columns.put("column2", storIOSQLiteColumnMeta2);

        final JavaFile javaFile = putResolverGenerator.generateJavaFile(storIOSQLiteTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        assertEquals("package com.test;\n" +
                "\n" +
                "import android.content.ContentValues;\n" +
                "import android.support.annotation.NonNull;\n" +
                "import com.pushtorefresh.storio.sqlite.operation.put.DefaultPutResolver;\n" +
                "import com.pushtorefresh.storio.sqlite.query.InsertQuery;\n" +
                "import com.pushtorefresh.storio.sqlite.query.UpdateQuery;\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "/**\n" +
                " * Generated resolver for Put Operation\n" +
                " */\n" +
                "public class TestItemPutResolver extends DefaultPutResolver<TestItem> {\n" +
                "    @Override\n" +
                "    @NonNull\n" +
                "    protected InsertQuery mapToInsertQuery(@NonNull TestItem object) {\n" +
                "        return new InsertQuery.Builder()\n" +
                "            .table(\"test_table\")\n" +
                "            .build();\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    @NonNull\n" +
                "    protected UpdateQuery mapToUpdateQuery(@NonNull TestItem object) {\n" +
                "        return new UpdateQuery.Builder()\n" +
                "            .table(\"test_table\")\n" +
                "            .where(\"column1 = ?\")\n" +
                "            .whereArgs(object.column1Field)\n" +
                "            .build();\n" +
                "    }\n" +
                "\n" +
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
                "}\n", out.toString());
    }
}
