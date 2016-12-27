package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

import static com.pushtorefresh.storio.sqlite.annotations.processor.generate.TestFactory.createColumnMetaMock;
import static com.pushtorefresh.storio.sqlite.annotations.processor.generate.TestFactory.createElementMock;
import static com.pushtorefresh.storio.sqlite.annotations.processor.generate.TestFactory.createNonNullElementMock;
import static javax.lang.model.type.TypeKind.NONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TableGeneratorTest {

    @NotNull
    private static final String PART_PACKAGE = "package com.test;\n\n";

    @NotNull
    private static final String PART_IMPORT =
            "import android.database.sqlite.SQLiteDatabase;\n" +
                    "import java.lang.String;\n" +
                    "\n";

    @NotNull
    private static final String PART_CLASS =
            "public final class TestItemTable {\n";

    @NotNull
    private static final String PART_FIELDS =
            "    public static final String tableName = \"test_table\";\n\n" +
                    "    public static final String COLUMN_column1Field = \"column1\";\n\n" +
                    "    public static final String COLUMN_column2Field = \"column2\";\n\n" +
                    "    public static final String COLUMN_column3Field = \"column3\";\n\n" +
                    "    public static final String COLUMN_column4Field = \"column4\";\n\n" +
                    "    public static final String COLUMN_column5Field = \"column5\";\n\n" +
                    "    public static final String COLUMN_column6Field = \"column6\";\n\n" +
                    "    public static final String COLUMN_column7Field = \"column7\";\n" +
                    "\n";

    @NotNull
    private static final String PART_CONSTRUCTOR = "    private TestItemTable() {\n" +
            "    }\n\n";

    @NotNull
    private static final String PART_CREATE_METHOD =
            "    public static void createTable(SQLiteDatabase db) {\n" +
                    "        db.execSQL(\"CREATE TABLE test_table (column1 INTEGER PRIMARY KEY,\\n\"\n" +
                    "                        + \"column2 INTEGER,\\n\"\n" +
                    "                        + \"column3 INTEGER,\\n\"\n" +
                    "                        + \"column4 INTEGER,\\n\"\n" +
                    "                        + \"column5 REAL,\\n\"\n" +
                    "                        + \"column6 REAL,\\n\"\n" +
                    "                        + \"column7 TEXT NOT NULL);\");\n" +
                    "    }\n\n";

    @NotNull
    private static final String PART_UPDATE_METHOD =
            "    public static void updateTable(SQLiteDatabase db, int versionOld) {\n" +
                    "        if(versionOld < 2) {\n" +
                    "            db.execSQL(\"ALTER TABLE test_table ADD COLUMN column7 TEXT NOT NULL;\");\n" +
                    "        }\n" +
                    "        if(versionOld < 3) {\n" +
                    "            db.execSQL(\"ALTER TABLE test_table ADD COLUMN column6 REAL;\");\n" +
                    "        }\n" +
                    "    }\n";

    @Test
    public void testHappyPath() throws IOException {
        final StorIOSQLiteType storIOSQLiteType = mock(StorIOSQLiteType.class);
        when(storIOSQLiteType.table()).thenReturn("test_table");
        when(storIOSQLiteType.generateTableClass()).thenReturn(true);
        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta("TestItem", "com.test", storIOSQLiteType, false);
        //int object with primary key
        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta1 = createColumnMetaMock(
                createElementMock(NONE),
                "column1",
                "column1Field",
                true,
                false,
                1,
                JavaType.INTEGER);
        storIOSQLiteTypeMeta.columns.put("column1", storIOSQLiteColumnMeta1);
        //LONG java type is generated as INTEGER type in SQL
        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta2 = createColumnMetaMock(
                createElementMock(NONE),
                "column2",
                "column2Field",
                false,
                false,
                1,
                JavaType.LONG);
        storIOSQLiteTypeMeta.columns.put("column2", storIOSQLiteColumnMeta2);
        //BOOLEAN java type is generated as INTEGER type in SQL
        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta3 = createColumnMetaMock(
                createElementMock(NONE),
                "column3",
                "column3Field",
                false,
                false,
                1,
                JavaType.LONG);
        storIOSQLiteTypeMeta.columns.put("column3", storIOSQLiteColumnMeta3);
        //SHORT java type is generated as INTEGER type in SQL
        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta4 = createColumnMetaMock(
                createElementMock(NONE),
                "column4",
                "column4Field",
                false,
                false,
                1,
                JavaType.SHORT);
        storIOSQLiteTypeMeta.columns.put("column4", storIOSQLiteColumnMeta4);
        //FLOAT java type is generated as REAL type in SQL
        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta5 = createColumnMetaMock(
                createElementMock(NONE),
                "column5",
                "column5Field",
                false,
                false,
                1,
                JavaType.FLOAT);
        storIOSQLiteTypeMeta.columns.put("column5", storIOSQLiteColumnMeta5);
        //DOUBLE java type is generated as REAL type in SQL
        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta6 = createColumnMetaMock(
                createElementMock(NONE),
                "column6",
                "column6Field",
                false,
                false,
                3,
                JavaType.DOUBLE);
        storIOSQLiteTypeMeta.columns.put("column6", storIOSQLiteColumnMeta6);
        //STRING java type is generated as TEXT type in SQL
        //Also test if NonNull annotation is generated as NOT NULL in SQL
        final StorIOSQLiteColumnMeta storIOSQLiteColumnMeta7 = createColumnMetaMock(
                createNonNullElementMock(NONE),
                "column7",
                "column7Field",
                false,
                false,
                2,
                JavaType.STRING);
        storIOSQLiteTypeMeta.columns.put("column7", storIOSQLiteColumnMeta7);
        final TableGenerator generator = new TableGenerator();
        final JavaFile javaFile = generator.generateJavaFile(storIOSQLiteTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);
        assertThat(out.toString()).isEqualTo(
                PART_PACKAGE +
                        PART_IMPORT +
                        PART_CLASS +
                        PART_FIELDS +
                        PART_CONSTRUCTOR +
                        PART_CREATE_METHOD +
                        PART_UPDATE_METHOD +
                        "}\n");
    }

    @Test
    public void testIfReturnsNullWhenGenerateTableIsSetToFalse() {
        final StorIOSQLiteType storIOSQLiteType = mock(StorIOSQLiteType.class);
        when(storIOSQLiteType.table()).thenReturn("test_table");
        when(storIOSQLiteType.generateTableClass()).thenReturn(false);
        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta("TestItem", "com.test", storIOSQLiteType, false);
        final TableGenerator generator = new TableGenerator();
        final JavaFile javaFile = generator.generateJavaFile(storIOSQLiteTypeMeta);
        assertThat(javaFile).isNull();
    }
}