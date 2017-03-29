package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import static com.pushtorefresh.storio.common.annotations.processor.generate.Common.INSTANCE;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class PutResolverGenerator implements Generator<StorIOSQLiteTypeMeta> {

    public static final String SUFFIX = "StorIOSQLitePutResolver";

    @NotNull
    public static String generateName(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta) {
        return storIOSQLiteTypeMeta.getSimpleName() + SUFFIX;
    }

    @NotNull
    public JavaFile generateJavaFile(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta) {
        final ClassName storIOSQLiteTypeClassName = ClassName.get(
            storIOSQLiteTypeMeta.getPackageName(), storIOSQLiteTypeMeta.getSimpleName());

        final TypeSpec putResolver = TypeSpec.classBuilder(generateName(storIOSQLiteTypeMeta))
                .addJavadoc("Generated resolver for Put Operation.\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.sqlite.operations.put", "DefaultPutResolver"), storIOSQLiteTypeClassName))
                .addMethod(createMapToInsertQueryMethodSpec(storIOSQLiteTypeMeta, storIOSQLiteTypeClassName))
                .addMethod(createMapToUpdateQueryMethodSpec(storIOSQLiteTypeMeta, storIOSQLiteTypeClassName))
                .addMethod(createMapToContentValuesMethodSpec(storIOSQLiteTypeMeta, storIOSQLiteTypeClassName))
                .build();

        return JavaFile
                .builder(storIOSQLiteTypeMeta.getPackageName(), putResolver)
                .indent(INSTANCE.getINDENT())
                .build();
    }

    @NotNull
    private MethodSpec createMapToInsertQueryMethodSpec(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta, @NotNull ClassName storIOSQLiteTypeClassName) {
        return MethodSpec.methodBuilder("mapToInsertQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio.sqlite.queries", "InsertQuery"))
                .addParameter(ParameterSpec.builder(storIOSQLiteTypeClassName, "object")
                        .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                        .build())
                .addCode("return InsertQuery.builder()\n" +
                        INSTANCE.getINDENT() + ".table($S)\n" +
                        INSTANCE.getINDENT() + ".build();\n",
                        storIOSQLiteTypeMeta.getStorIOType().table())
                .build();
    }

    @NotNull
    private MethodSpec createMapToUpdateQueryMethodSpec(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta, @NotNull ClassName storIOSQLiteTypeClassName) {
        final Map<String, String> where = QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object");

        return MethodSpec.methodBuilder("mapToUpdateQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio.sqlite.queries", "UpdateQuery"))
                .addParameter(ParameterSpec.builder(storIOSQLiteTypeClassName, "object")
                        .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                        .build())
                .addCode("return UpdateQuery.builder()\n" +
                        INSTANCE.getINDENT() + ".table($S)\n" +
                        INSTANCE.getINDENT() + ".where($S)\n" +
                        INSTANCE.getINDENT() + ".whereArgs($L)\n" +
                        INSTANCE.getINDENT() + ".build();\n",
                        storIOSQLiteTypeMeta.getStorIOType().table(),
                        where.get(QueryGenerator.WHERE_CLAUSE),
                        where.get(QueryGenerator.WHERE_ARGS))
                .build();
    }

    @NotNull
    private MethodSpec createMapToContentValuesMethodSpec(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta, @NotNull ClassName storIOSQLiteTypeClassName) {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("mapToContentValues")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                .addModifiers(PUBLIC)
                .returns(ClassName.get("android.content", "ContentValues"))
                .addParameter(ParameterSpec.builder(storIOSQLiteTypeClassName, "object")
                        .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                        .build())
                .addStatement("ContentValues contentValues = new ContentValues($L)",
                    storIOSQLiteTypeMeta
                    .getColumns().size())
                .addCode("\n");

        for (StorIOSQLiteColumnMeta columnMeta : storIOSQLiteTypeMeta.getColumns().values()) {
            final boolean ignoreNull = columnMeta.getStorIOColumn().ignoreNull();
            if (ignoreNull) {
                builder.beginControlFlow("if (object.$L != null)", columnMeta.getElementName() + (columnMeta.isMethod() ? "()" : ""));
            }
            builder.addStatement(
                    "contentValues.put($S, object.$L)",
                    columnMeta.getStorIOColumn().name(),
                    columnMeta.getElementName() + (columnMeta.isMethod() ? "()" : "")
            );
            if (ignoreNull) {
                builder.endControlFlow();
            }
        }

        return builder
                .addCode("\n")
                .addStatement("return contentValues")
                .build();
    }
}
