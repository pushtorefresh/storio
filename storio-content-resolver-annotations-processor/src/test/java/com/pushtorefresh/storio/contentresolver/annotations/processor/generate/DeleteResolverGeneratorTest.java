package com.pushtorefresh.storio.contentresolver.annotations.processor.generate;

import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverColumn;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverType;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;
import com.squareup.javapoet.JavaFile;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeleteResolverGeneratorTest {

    @NotNull
    private String generateJavaFile(
            @NotNull StorIOContentResolverType storIOContentResolverType) throws IOException {

        final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta = new StorIOContentResolverTypeMeta(
                "TestItem",
                "com.test",
                storIOContentResolverType
        );

        final StorIOContentResolverColumn storIOContentResolverColumn1 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn1.name()).thenReturn("column1");

        //noinspection ConstantConditions
        final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta1 = new StorIOContentResolverColumnMeta(
                null,
                null,
                "field1",
                null,
                storIOContentResolverColumn1
        );
        storIOContentResolverTypeMeta.columns.put("column1", storIOContentResolverColumnMeta1);

        final StorIOContentResolverColumn storIOContentResolverColumn2 = mock(StorIOContentResolverColumn.class);
        when(storIOContentResolverColumn2.name()).thenReturn("column2");

        //noinspection ConstantConditions
        final StorIOContentResolverColumnMeta storIOContentResolverColumnMeta2 = new StorIOContentResolverColumnMeta(
                null,
                null,
                "field2",
                null,
                storIOContentResolverColumn2
        );
        storIOContentResolverTypeMeta.columns.put("column2", storIOContentResolverColumnMeta2);

        final JavaFile javaFile = new DeleteResolverGenerator().generateJavaFile(storIOContentResolverTypeMeta);
        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);

        return out.toString();
    }

    @Test
    public void generateJavaFileWithCommonUri() throws IOException {
        final StorIOContentResolverType storIOContentResolverType = mock(StorIOContentResolverType.class);

        when(storIOContentResolverType.uri()).thenReturn("content://test");

        String javaFileAsString = generateJavaFile(storIOContentResolverType);
        assertThat(javaFileAsString).isEqualTo("package com.test;\n" +
                "\n" +
                "import android.support.annotation.NonNull;\n" +
                "import com.pushtorefresh.storio.contentresolver.operations.delete.DefaultDeleteResolver;\n" +
                "import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "/**\n" +
                " * Generated resolver for Delete Operation\n" +
                " */\n" +
                "public class TestItemStorIOContentResolverDeleteResolver extends DefaultDeleteResolver<TestItem> {\n" +
                "    /**\n" +
                "     * {@inheritDoc}\n" +
                "     */\n" +
                "    @Override\n" +
                "    @NonNull\n" +
                "    public DeleteQuery mapToDeleteQuery(@NonNull TestItem object) {\n" +
                "        return DeleteQuery.builder()\n" +
                "            .uri(\"content://test\")\n" +
                "            .where(null)\n" +
                "            .whereArgs(null)\n" +
                "            .build();\n" +
                "    }\n" +
                "}\n");
    }

    @Test
    public void generateJavaFileWithOperationSpecificUri() throws IOException {
        final StorIOContentResolverType storIOContentResolverType = mock(StorIOContentResolverType.class);

        when(storIOContentResolverType.deleteUri()).thenReturn("content://delete_test");

        String javaFileAsString = generateJavaFile(storIOContentResolverType);
        assertThat(javaFileAsString).isEqualTo("package com.test;\n" +
                "\n" +
                "import android.support.annotation.NonNull;\n" +
                "import com.pushtorefresh.storio.contentresolver.operations.delete.DefaultDeleteResolver;\n" +
                "import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "/**\n" +
                " * Generated resolver for Delete Operation\n" +
                " */\n" +
                "public class TestItemStorIOContentResolverDeleteResolver extends DefaultDeleteResolver<TestItem> {\n" +
                "    /**\n" +
                "     * {@inheritDoc}\n" +
                "     */\n" +
                "    @Override\n" +
                "    @NonNull\n" +
                "    public DeleteQuery mapToDeleteQuery(@NonNull TestItem object) {\n" +
                "        return DeleteQuery.builder()\n" +
                "            .uri(\"content://delete_test\")\n" +   // Operation specific
                "            .where(null)\n" +
                "            .whereArgs(null)\n" +
                "            .build();\n" +
                "    }\n" +
                "}\n");
    }

    @Test
    public void operationSpecificUriShouldHaveHigherPriority() throws IOException {
        final StorIOContentResolverType storIOContentResolverType = mock(StorIOContentResolverType.class);

        when(storIOContentResolverType.uri()).thenReturn("content://test");
        when(storIOContentResolverType.deleteUri()).thenReturn("content://delete_test");

        String javaFileAsString = generateJavaFile(storIOContentResolverType);
        assertThat(javaFileAsString).isEqualTo("package com.test;\n" +
                "\n" +
                "import android.support.annotation.NonNull;\n" +
                "import com.pushtorefresh.storio.contentresolver.operations.delete.DefaultDeleteResolver;\n" +
                "import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "/**\n" +
                " * Generated resolver for Delete Operation\n" +
                " */\n" +
                "public class TestItemStorIOContentResolverDeleteResolver extends DefaultDeleteResolver<TestItem> {\n" +
                "    /**\n" +
                "     * {@inheritDoc}\n" +
                "     */\n" +
                "    @Override\n" +
                "    @NonNull\n" +
                "    public DeleteQuery mapToDeleteQuery(@NonNull TestItem object) {\n" +
                "        return DeleteQuery.builder()\n" +
                "            .uri(\"content://delete_test\")\n" +   // Operation specific
                "            .where(null)\n" +
                "            .whereArgs(null)\n" +
                "            .build();\n" +
                "    }\n" +
                "}\n");
    }

    @Test
    public void shouldUseCommonUriIfSpecifiedOnlyForAnotherOperations() throws IOException {
        final StorIOContentResolverType storIOContentResolverType = mock(StorIOContentResolverType.class);

        when(storIOContentResolverType.uri()).thenReturn("content://test");
        when(storIOContentResolverType.insertUri()).thenReturn("content://insert_test");
        when(storIOContentResolverType.updateUri()).thenReturn("content://update_test");
        // There is no explicit uri for delete operation

        String javaFileAsString = generateJavaFile(storIOContentResolverType);
        assertThat(javaFileAsString).isEqualTo("package com.test;\n" +
                "\n" +
                "import android.support.annotation.NonNull;\n" +
                "import com.pushtorefresh.storio.contentresolver.operations.delete.DefaultDeleteResolver;\n" +
                "import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "/**\n" +
                " * Generated resolver for Delete Operation\n" +
                " */\n" +
                "public class TestItemStorIOContentResolverDeleteResolver extends DefaultDeleteResolver<TestItem> {\n" +
                "    /**\n" +
                "     * {@inheritDoc}\n" +
                "     */\n" +
                "    @Override\n" +
                "    @NonNull\n" +
                "    public DeleteQuery mapToDeleteQuery(@NonNull TestItem object) {\n" +
                "        return DeleteQuery.builder()\n" +
                "            .uri(\"content://test\")\n" +   // Common uri
                "            .where(null)\n" +
                "            .whereArgs(null)\n" +
                "            .build();\n" +
                "    }\n" +
                "}\n");
    }
}
