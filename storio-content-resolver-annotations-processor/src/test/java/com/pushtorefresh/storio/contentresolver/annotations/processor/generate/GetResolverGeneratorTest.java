package com.pushtorefresh.storio.contentresolver.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverType;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

import javax.lang.model.type.TypeKind;

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
                    "import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;\n" +
                    "import java.lang.Override;\n" +
                    "\n";

    @NotNull
    private static final String PART_CLASS =
            "/**\n" +
                    " * Generated resolver for Get Operation\n" +
                    " */\n" +
                    "public class TestItemStorIOContentResolverGetResolver extends DefaultGetResolver<TestItem> {\n";

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
        final StorIOContentResolverType storIOContentResolverType = mock(StorIOContentResolverType.class);

        when(storIOContentResolverType.uri()).thenReturn("content://test");

        final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = new StorIOContentResolverTypeMeta(
                "TestItem",
                "com.test",
                storIOContentResolverType
        );

        final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta1 = TestFactory.createColumnMetaMock(
                TestFactory.createElementMock(TypeKind.BOOLEAN),
                "column1",
                "field1",
                true,
                false,
                JavaType.BOOLEAN
        );
        storIOContentResolverTypeMeta.columns.put("column1", storIOContentResolverColumnMeta1);

        final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta2 = TestFactory.createColumnMetaMock(
                TestFactory.createElementMock(TypeKind.OTHER),
                "column2",
                "field2",
                false,
                false,
                JavaType.STRING
        );
        storIOContentResolverTypeMeta.columns.put("column2", storIOContentResolverColumnMeta2);

        final JavaFile javaFile = new GetResolverGenerator().generateJavaFile(storIOContentResolverTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        checkFile(out.toString(),
                PART_PACKAGE,
                PART_IMPORT,
                PART_CLASS,
                PART_MAP_FROM_CURSOR_WITHOUT_NULL_CHECK);
    }

    @Test
    public void checksForNullIfBoxedType() throws IOException {
        final StorIOContentResolverType storIOContentResolverType = mock(StorIOContentResolverType.class);

        when(storIOContentResolverType.uri()).thenReturn("content://test");

        final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = new StorIOContentResolverTypeMeta(
                "TestItem",
                "com.test",
                storIOContentResolverType
        );

        final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta1 = TestFactory.createColumnMetaMock(
                TestFactory.createElementMock(TypeKind.BOOLEAN),
                "column1",
                "field1",
                true,
                false,
                JavaType.BOOLEAN
        );
        storIOContentResolverTypeMeta.columns.put("column1", storIOContentResolverColumnMeta1);

        final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta2 = TestFactory.createColumnMetaMock(
                TestFactory.createElementMock(TypeKind.OTHER),
                "column2",
                "field2",
                false,
                false,
                JavaType.INTEGER_OBJECT     // boxed type
        );
        storIOContentResolverTypeMeta.columns.put("column2", storIOContentResolverColumnMeta2);

        final JavaFile javaFile = new GetResolverGenerator().generateJavaFile(storIOContentResolverTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        checkFile(out.toString(),
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
