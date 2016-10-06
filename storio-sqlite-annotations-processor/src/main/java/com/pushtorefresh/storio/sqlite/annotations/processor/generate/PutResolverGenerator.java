package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.pushtorefresh.storio.common.annotations.processor.generate.Common.ANDROID_NON_NULL_ANNOTATION_CLASS_NAME;
import static com.pushtorefresh.storio.common.annotations.processor.generate.Common.INDENT;
import static javax.lang.model.element.Modifier.PUBLIC;

public class PutResolverGenerator implements Generator<StorIOSQLiteTypeMeta> {

    public static final String SUFFIX = "StorIOSQLitePutResolver";

    @NotNull
    public static String generateName(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta) {
        return storIOSQLiteTypeMeta.simpleName + SUFFIX;
    }

    @NotNull
    public JavaFile generateJavaFile(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta) {
        final ClassName storIOSQLiteTypeClassName = ClassName.get(storIOSQLiteTypeMeta.packageName, storIOSQLiteTypeMeta.simpleName);

        final TypeSpec putResolver = TypeSpec.classBuilder(generateName(storIOSQLiteTypeMeta))
                .addJavadoc("Generated resolver for Put Operation\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.sqlite.operations.put", "DefaultPutResolver"), storIOSQLiteTypeClassName))
                .addMethod(createMapToInsertQueryMethodSpec(storIOSQLiteTypeMeta, storIOSQLiteTypeClassName))
                .addMethod(createMapToUpdateQueryMethodSpec(storIOSQLiteTypeMeta, storIOSQLiteTypeClassName))
                .addMethod(createMapToContentValuesMethodSpec(storIOSQLiteTypeMeta, storIOSQLiteTypeClassName))
                .build();

        return JavaFile
                .builder(storIOSQLiteTypeMeta.packageName, putResolver)
                .indent(INDENT)
                .build();
    }

    @NotNull
    private MethodSpec createMapToInsertQueryMethodSpec(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta, @NotNull ClassName storIOSQLiteTypeClassName) {
        return MethodSpec.methodBuilder("mapToInsertQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio.sqlite.queries", "InsertQuery"))
                .addParameter(ParameterSpec.builder(storIOSQLiteTypeClassName, "object")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addCode("return InsertQuery.builder()\n" +
                                INDENT + ".table($S)\n" +
                                INDENT + ".build();\n",
                        storIOSQLiteTypeMeta.storIOType.table())
                .build();
    }

    @NotNull
    private MethodSpec createMapToUpdateQueryMethodSpec(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta, @NotNull ClassName storIOSQLiteTypeClassName) {
        final Map<String, String> where = QueryGenerator.createWhere(storIOSQLiteTypeMeta, "object");

        return MethodSpec.methodBuilder("mapToUpdateQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio.sqlite.queries", "UpdateQuery"))
                .addParameter(ParameterSpec.builder(storIOSQLiteTypeClassName, "object")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addCode("return UpdateQuery.builder()\n" +
                                INDENT + ".table($S)\n" +
                                INDENT + ".where($S)\n" +
                                INDENT + ".whereArgs($L)\n" +
                                INDENT + ".build();\n",
                        storIOSQLiteTypeMeta.storIOType.table(),
                        where.get(QueryGenerator.WHERE_CLAUSE),
                        where.get(QueryGenerator.WHERE_ARGS))
                .build();
    }

    @NotNull
    private MethodSpec createMapToContentValuesMethodSpec(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta, @NotNull ClassName storIOSQLiteTypeClassName) {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("mapToContentValues")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("android.content", "ContentValues"))
                .addParameter(ParameterSpec.builder(storIOSQLiteTypeClassName, "object")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addStatement("ContentValues contentValues = new ContentValues($L)", storIOSQLiteTypeMeta.columns.size())
                .addCode("\n");

        for (StorIOSQLiteColumnMeta columnMeta : storIOSQLiteTypeMeta.columns.values()) {
            final boolean ignoreNull = columnMeta.storIOColumn.ignoreNull();
            if (ignoreNull) {
                builder.beginControlFlow("if($L != null)", "object." + columnMeta.fieldName);
            }
            builder.addStatement(
                    "contentValues.put($S, $L)",
                    columnMeta.storIOColumn.name(),
                    "object." + columnMeta.fieldName
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
