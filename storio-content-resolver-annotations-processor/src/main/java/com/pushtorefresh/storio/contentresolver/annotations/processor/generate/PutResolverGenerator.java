package com.pushtorefresh.storio.contentresolver.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;
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

public class PutResolverGenerator implements Generator<StorIOContentResolverTypeMeta> {

    private static final String SUFFIX = "StorIOContentResolverPutResolver";

    @NotNull
    public static String generateName(@NotNull StorIOContentResolverTypeMeta storIOSQLiteTypeMeta) {
        return storIOSQLiteTypeMeta.simpleName + SUFFIX;
    }

    @NotNull
    public JavaFile generateJavaFile(@NotNull final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta) {
        final ClassName storIOContentResolverTypeClassName = ClassName.get(storIOContentResolverTypeMeta.packageName, storIOContentResolverTypeMeta.simpleName);

        final TypeSpec putResolver = TypeSpec.classBuilder(generateName(storIOContentResolverTypeMeta))
                .addJavadoc("Generated resolver for Put Operation\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.contentresolver.operations.put", "DefaultPutResolver"), storIOContentResolverTypeClassName))
                .addMethod(createMapToInsertQueryMethodSpec(storIOContentResolverTypeMeta, storIOContentResolverTypeClassName))
                .addMethod(createMapToUpdateQueryMethodSpec(storIOContentResolverTypeMeta, storIOContentResolverTypeClassName))
                .addMethod(createMapToContentValuesMethodSpec(storIOContentResolverTypeMeta, storIOContentResolverTypeClassName))
                .build();

        return JavaFile
                .builder(storIOContentResolverTypeMeta.packageName, putResolver)
                .indent(INDENT)
                .build();

    }

    @NotNull
    private MethodSpec createMapToInsertQueryMethodSpec(@NotNull final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta, @NotNull final ClassName storIOContentResolverClassName) {
        String insertUri = storIOContentResolverTypeMeta.storIOType.insertUri();
        if (insertUri == null || insertUri.length() == 0) {
            insertUri = storIOContentResolverTypeMeta.storIOType.uri();
        }

        return MethodSpec.methodBuilder("mapToInsertQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio.contentresolver.queries", "InsertQuery"))
                .addParameter(ParameterSpec.builder(storIOContentResolverClassName, "object")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addCode("return InsertQuery.builder()\n" +
                                INDENT + ".uri($S)\n" +
                                INDENT + ".build();\n",
                        insertUri)
                .build();
    }

    @NotNull
    private MethodSpec createMapToUpdateQueryMethodSpec(@NotNull final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta, @NotNull final ClassName storIOContentResolverClassName) {
        final Map<String, String> where = QueryGenerator.createWhere(storIOContentResolverTypeMeta, "object");

        String updateUri = storIOContentResolverTypeMeta.storIOType.updateUri();
        if (updateUri == null || updateUri.length() == 0) {
            updateUri = storIOContentResolverTypeMeta.storIOType.uri();
        }

        return MethodSpec.methodBuilder("mapToUpdateQuery")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("com.pushtorefresh.storio.contentresolver.queries", "UpdateQuery"))
                .addParameter(ParameterSpec.builder(storIOContentResolverClassName, "object")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addCode("return UpdateQuery.builder()\n" +
                                INDENT + ".uri($S)\n" +
                                INDENT + ".where($S)\n" +
                                INDENT + ".whereArgs($L)\n" +
                                INDENT + ".build();\n",
                        updateUri,
                        where.get(QueryGenerator.WHERE_CLAUSE),
                        where.get(QueryGenerator.WHERE_ARGS))
                .build();
    }

    @NotNull
    private MethodSpec createMapToContentValuesMethodSpec(@NotNull final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta, @NotNull final ClassName storIOContentResolverClassName) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("mapToContentValues")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(ClassName.get("android.content", "ContentValues"))
                .addParameter(ParameterSpec.builder(storIOContentResolverClassName, "object")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addStatement("ContentValues contentValues = new ContentValues($L)", storIOContentResolverTypeMeta.columns.size())
                .addCode("\n");

        for (final StorIOContentResolverColumnMeta columnMeta : storIOContentResolverTypeMeta.columns.values()) {
            final boolean ignoreNull = columnMeta.storIOColumn.ignoreNull();
            if (ignoreNull) {
                builder.beginControlFlow("if($L != null)", "object." + columnMeta.elementName);
            }
            builder.addStatement(
                    "contentValues.put($S, $L)",
                    columnMeta.storIOColumn.name(),
                    "object." + columnMeta.elementName + (columnMeta.isMethod() ? "()" : "")
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
