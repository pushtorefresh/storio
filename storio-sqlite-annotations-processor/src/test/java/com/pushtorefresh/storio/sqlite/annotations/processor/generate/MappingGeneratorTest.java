package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MappingGeneratorTest {
    @Test
    public void generateJavaFile() throws IOException {
        final StorIOSQLiteType storIOSQLiteType = mock(StorIOSQLiteType.class);

        final StorIOSQLiteTypeMeta storIOSQLiteTypeMeta = new StorIOSQLiteTypeMeta(
                "TestItem",
                "com.test",
                storIOSQLiteType,
                false
        );

        MappingGenerator mappingGenerator = new MappingGenerator();
        final JavaFile javaFile = mappingGenerator.generateJavaFile(storIOSQLiteTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);


        String result =
                "package com.test;\n" +
                "\n" +
                "import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;\n" +
                "\n" +
                "/**\n" +
                " * Generated mapping with collection of resolvers\n" +
                " */\n" +
                "public class TestItemSQLiteTypeMapping extends SQLiteTypeMapping<TestItem> {\n" +
                "    public TestItemSQLiteTypeMapping() {\n" +
                "        super(new TestItemStorIOSQLitePutResolver(),\n" +
                "                new TestItemStorIOSQLiteGetResolver(),\n" +
                "                new TestItemStorIOSQLiteDeleteResolver());\n" +
                "    }\n" +
                "}\n";

        assertThat(out.toString()).isEqualTo(result);
    }
}