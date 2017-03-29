package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import static com.pushtorefresh.storio.common.annotations.processor.generate.Common.INSTANCE;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class DeleteResolverGenerator implements Generator<StorIOSQLiteTypeMeta> {

    public static final String SUFFIX = "StorIOSQLiteDeleteResolver";

    @NotNull
    public static String generateName(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta) {
        return storIOSQLiteTypeMeta.getSimpleName() + SUFFIX;
    }

    @NotNull
    public JavaFile generateJavaFile(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta) {
        final ClassName storIOSQLiteTypeClassName = ClassName.get(
            storIOSQLiteTypeMeta.getPackageName(), storIOSQLiteTypeMeta.getSimpleName());

        final TypeSpec deleteResolver = TypeSpec.classBuilder(generateName(storIOSQLiteTypeMeta))
                .addJavadoc("Generated resolver for Delete Operation.\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.sqlite.operations.delete", "DefaultDeleteResolver"), storIOSQLiteTypeClassName))
                .addMethod(createMapToDeleteQueryMethodSpec(storIOSQLiteTypeMeta, storIOSQLiteTypeClassName))
                .build();

        return JavaFile
                .builder(storIOSQLiteTypeMeta.getPackageName(), deleteResolver)
                .indent(INSTANCE.getINDENT())
                .build();
    }

    @NotNull
    private MethodSpec createMapToDeleteQueryMethodSpec(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta, @NotNull ClassName storIOSQLiteTypeClassName) {
        final Map<String, String> where = QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object");

        return MethodSpec.methodBuilder("mapToDeleteQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio.sqlite.queries", "DeleteQuery"))
                .addParameter(ParameterSpec.builder(storIOSQLiteTypeClassName, "object")
                        .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                        .build())
                .addCode("return DeleteQuery.builder()\n" +
                        INSTANCE.getINDENT() + ".table($S)\n" +
                        INSTANCE.getINDENT() + ".where($S)\n" +
                        INSTANCE.getINDENT() + ".whereArgs($L)\n" +
                        INSTANCE.getINDENT() + ".build();\n",
                        storIOSQLiteTypeMeta.getStorIOType().table(),
                        where.get(QueryGenerator.WHERE_CLAUSE),
                        where.get(QueryGenerator.WHERE_ARGS))
                .build();
    }
}
