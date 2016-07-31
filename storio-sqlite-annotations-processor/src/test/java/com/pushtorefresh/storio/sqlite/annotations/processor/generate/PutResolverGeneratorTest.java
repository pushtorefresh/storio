package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static com.pushtorefresh.storio.sqlite.annotations.processor.generate.TestFactory.createColumnMetaMock;
import static javax.lang.model.type.TypeKind.NONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PutResolverGeneratorTest {

    @NotNull
    private static final String PART_PACKAGE = "package com.test;\n\n";

    @NotNull
    private static final String PART_IMPORT =
            "import android.content.ContentValues;\n" +
                    "import android.support.annotation.NonNull;\n" +
                    "import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;\n" +
                    "import com.pushtorefresh.storio.sqlite.queries.InsertQuery;\n" +
                    "import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;\n" +
                    "import java.lang.Override;\n" +
                    "\n";

    @NotNull
    private static final String PART_CLASS =
            "/**\n" +
                    " * Generated resolver for Put Operation\n" +
                    " */\n" +
                    "public class TestItemStorIOSQLitePutResolver extends DefaultPutResolver<TestItem> {\n";

    @NotNull
    private static final String PART_MAP_TO_INSERT_QUERY =
            "    /**\n" +
                    "     * {@inheritDoc}\n" +
                    "     */\n" +
                    "    @Override\n" +
                    "    @NonNull\n" +
                    "    public InsertQuery mapToInsertQuery(@NonNull TestItem object) {\n" +
                    "        return InsertQuery.builder()\n" +
                    "            .table(\"test_table\")\n" +
                    "            .build();\n" +
                    "    }\n" +
                    "\n";

    @NotNull
    private static final String PART_MAP_TO_UPDATE_QUERY =
            "    /**\n" +
                    "     * {@inheritDoc}\n" +
                    "     */\n" +
                    "    @Override\n" +
                    "    @NonNull\n" +
                    "    public UpdateQuery mapToUpdateQuery(@NonNull TestItem object) {\n" +
                    "        return UpdateQuery.builder()\n" +
                    "            .table(\"test_table\")\n" +
                    "            .where(\"column1 = ?\")\n" +
                    "            .whereArgs(object.column1Field)\n" +
                    "            .build();\n" +
                    "    }\n" +
                    "\n";

    @NotNull
    private static final String PART_MAP_TO_CONTENT_VALUES_WITHOUT_NULL_CHECK =
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
                    "    }\n";

    @Test
    public void generateJavaFile() throws IOException {
        final StorIOSQLiteType storIOSQLiteType = mock(StorIOSQLiteType.class);

        when(storIOSQLiteType.table()).thenReturn("test_table");

        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta("TestItem", "com.test", storIOSQLiteType);

        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta1 = createColumnMetaMock(
                createElementMock(NONE),
                "column1",
                "column1Field",
                true,           // key
                false,
                null);
        storIOSQLiteTypeMeta.columns.put("column1", storIOSQLiteColumnMeta1);

        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta2 = createColumnMetaMock(
                createElementMock(NONE),
                "column2",
                "column2Field",
                false,
                false,
                null);
        storIOSQLiteTypeMeta.columns.put("column2", storIOSQLiteColumnMeta2);

        final PutResolverGenerator putResolverGenerator = new PutResolverGenerator();
        final JavaFile javaFile = putResolverGenerator.generateJavaFile(storIOSQLiteTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        checkFile(out.toString(),
                PART_PACKAGE,
                PART_IMPORT,
                PART_CLASS,
                PART_MAP_TO_INSERT_QUERY,
                PART_MAP_TO_UPDATE_QUERY,
                PART_MAP_TO_CONTENT_VALUES_WITHOUT_NULL_CHECK);
    }

    @Test
    public void ignoreNullsShouldAddCheck() throws IOException {
        final StorIOSQLiteType storIOSQLiteType = mock(StorIOSQLiteType.class);

        when(storIOSQLiteType.table()).thenReturn("test_table");

        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta("TestItem", "com.test", storIOSQLiteType);

        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta1 = createColumnMetaMock(
                createElementMock(NONE),
                "column1",
                "column1Field",
                true,
                false,
                null);
        storIOSQLiteTypeMeta.columns.put("column1", storIOSQLiteColumnMeta1);

        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta2 = createColumnMetaMock(
                createElementMock(NONE),    // not a primitive
                "column2",
                "column2Field",
                false,
                true,
                null);                      // ignore nulls
        storIOSQLiteTypeMeta.columns.put("column2", storIOSQLiteColumnMeta2);

        final PutResolverGenerator putResolverGenerator = new PutResolverGenerator();
        final JavaFile javaFile = putResolverGenerator.generateJavaFile(storIOSQLiteTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        checkFile(out.toString(),
                PART_PACKAGE,
                PART_IMPORT,
                PART_CLASS,
                PART_MAP_TO_INSERT_QUERY,
                PART_MAP_TO_UPDATE_QUERY,
                "    /**\n" +
                        "     * {@inheritDoc}\n" +
                        "     */\n" +
                        "    @Override\n" +
                        "    @NonNull\n" +
                        "    public ContentValues mapToContentValues(@NonNull TestItem object) {\n" +
                        "        ContentValues contentValues = new ContentValues(2);\n" +
                        "\n" +
                        "        contentValues.put(\"column1\", object.column1Field);\n" +
                        "        if(object.column2Field != null) {\n" +                         // check for null added
                        "            contentValues.put(\"column2\", object.column2Field);\n" +
                        "        }\n" +
                        "\n" +
                        "        return contentValues;\n" +
                        "    }\n");
    }

    @NotNull
    private static Element createElementMock(@NotNull TypeKind typeKind) {
        final Element objectElement = mock(Element.class);
        final TypeMirror typeMirror = mock(TypeMirror.class);
        when(objectElement.asType()).thenReturn(typeMirror);
        when(typeMirror.getKind()).thenReturn(typeKind);
        return objectElement;
    }

    private void checkFile(
            @NotNull String actualFile,
            @NotNull String partPackage,
            @NotNull String partImport,
            @NotNull String partClass,
            @NotNull String partMapToInsertQuery,
            @NotNull String partMapToUpdateQuery,
            @NotNull String partMapToContentValues
    ) {
        assertThat(actualFile).isEqualTo(
                partPackage +
                        partImport +
                        partClass +
                        partMapToInsertQuery +
                        partMapToUpdateQuery +
                        partMapToContentValues +
                        "}\n");
    }
}
