package com.pushtorefresh.storio.contentresolver.annotations.processor.generate;

import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverType;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MappingGeneratorTest {
    @Test
    public void generateJavaFile() throws IOException {
        final StorIOContentResolverType storIOSQLiteType = mock(StorIOContentResolverType.class);

        final StorIOContentResolverTypeMeta storIOSQLiteTypeMeta = new StorIOContentResolverTypeMeta(
                "TestItem",
                "com.test",
                storIOSQLiteType
        );

        MappingGenerator mappingGenerator = new MappingGenerator();
        final JavaFile javaFile = mappingGenerator.generateJavaFile(storIOSQLiteTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);


        String result = "package com.test;\n" +
                "\n" +
                "import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;\n" +
                "\n" +
                "/**\n" +
                " * Generated mapping with collection of resolvers\n" +
                " */\n" +
                "public class TestItemContentResolverTypeMapping extends ContentResolverTypeMapping<TestItem> {\n" +
                "    public TestItemContentResolverTypeMapping() {\n" +
                "        super(new TestItemStorIOContentResolverPutResolver(),\n" +
                "                new TestItemStorIOContentResolverGetResolver(),\n" +
                "                new TestItemStorIOContentResolverDeleteResolver());\n" +
                "    }\n" +
                "}\n";

        assertThat(out.toString()).isEqualTo(result);
    }
}