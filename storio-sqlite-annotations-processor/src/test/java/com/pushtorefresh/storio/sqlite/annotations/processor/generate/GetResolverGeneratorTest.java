package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

import javax.lang.model.type.TypeKind;

import static com.pushtorefresh.storio.sqlite.annotations.processor.generate.TestFactory.createColumnMetaMock;
import static com.pushtorefresh.storio.sqlite.annotations.processor.generate.TestFactory.createElementMock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetResolverGeneratorTest {

    @NotNull
    private static final String PART_PACKAGE = "package com.test;\n\n";

    @NotNull
    private static final String PART_IMPORT =
            "import android.database.Cursor;\n" +
                    "import android.support.annotation.NonNull;\n" +
                    "import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;\n" +
                    "import java.lang.Override;\n" +
                    "\n";

    @NotNull
    private static final String PART_CLASS =
            "/**\n" +
                    " * Generated resolver for Get Operation\n" +
                    " */\n" +
                    "public class TestItemStorIOSQLiteGetResolver extends DefaultGetResolver<TestItem> {\n";

    @NotNull
    private static final String PART_MAP_FROM_CURSOR_WITHOUT_NULL_CHECK =
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
                    "    }\n";

    @Test
    public void generateJavaFileTest() throws IOException {
        final StorIOSQLiteType storIOSQLiteType = mock(StorIOSQLiteType.class);

        when(storIOSQLiteType.table()).thenReturn("test_table");

        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(
                "TestItem",
                "com.test",
                storIOSQLiteType,
                false
        );

        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta1 = createColumnMetaMock(
                createElementMock(TypeKind.BOOLEAN),
                "column1",
                "field1",
                true,           // key
                false,
                JavaType.BOOLEAN
        );
        storIOSQLiteTypeMeta.columns.put("column1", storIOSQLiteColumnMeta1);

        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta2 = createColumnMetaMock(
                createElementMock(TypeKind.OTHER),
                "column2",
                "field2",
                false,
                false,
                JavaType.STRING
        );
        storIOSQLiteTypeMeta.columns.put("column2", storIOSQLiteColumnMeta2);

        final JavaFile javaFile = new GetResolverGenerator().generateJavaFile(storIOSQLiteTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        checkFile(
                out.toString(),
                PART_PACKAGE,
                PART_IMPORT,
                PART_CLASS,
                PART_MAP_FROM_CURSOR_WITHOUT_NULL_CHECK
        );
    }

    @Test
    public void checksForNullIfBoxedType() throws IOException {
        final StorIOSQLiteType storIOSQLiteType = mock(StorIOSQLiteType.class);

        when(storIOSQLiteType.table()).thenReturn("test_table");

        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(
                "TestItem",
                "com.test",
                storIOSQLiteType,
                false
        );

        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta1 = createColumnMetaMock(
                createElementMock(TypeKind.BOOLEAN),
                "column1",
                "field1",
                true,                       // key
                false,
                JavaType.BOOLEAN
        );
        storIOSQLiteTypeMeta.columns.put("column1", storIOSQLiteColumnMeta1);

        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta2 = createColumnMetaMock(
                createElementMock(TypeKind.OTHER),
                "column2",
                "field2",
                false,
                false,
                JavaType.INTEGER_OBJECT     // boxed type
        );
        storIOSQLiteTypeMeta.columns.put("column2", storIOSQLiteColumnMeta2);

        final JavaFile javaFile = new GetResolverGenerator().generateJavaFile(storIOSQLiteTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        checkFile(
                out.toString(),
                PART_PACKAGE,
                PART_IMPORT,
                PART_CLASS,
                "    /**\n" +
                        "     * {@inheritDoc}\n" +
                        "     */\n" +
                        "    @Override\n" +
                        "    @NonNull\n" +
                        "    public TestItem mapFromCursor(@NonNull Cursor cursor) {\n" +
                        "        TestItem object = new TestItem();\n" +
                        "\n" +
                        "        object.field1 = cursor.getInt(cursor.getColumnIndex(\"column1\")) == 1;\n" +
                        "        if (!cursor.isNull(cursor.getColumnIndex(\"column2\"))) {\n" +
                        "            object.field2 = cursor.getInt(cursor.getColumnIndex(\"column2\"));\n" +
                        "        }\n" +
                        "\n" +
                        "        return object;\n" +
                        "    }\n"
        );
    }

    private void checkFile(
            @NotNull String actualFile,
            @NotNull String partPackage,
            @NotNull String partImport,
            @NotNull String partClass,
            @NotNull String partMapFromCursor
    ) {
        assertThat(actualFile).isEqualTo(
                partPackage +
                        partImport +
                        partClass +
                        partMapFromCursor +
                        "}\n");
    }
}
