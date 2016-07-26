package com.pushtorefresh.storio.contentresolver.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.ProcessingException;
import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;

import static com.pushtorefresh.storio.common.annotations.processor.generate.Common.ANDROID_NON_NULL_ANNOTATION_CLASS_NAME;
import static com.pushtorefresh.storio.common.annotations.processor.generate.Common.INDENT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BOOLEAN;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BOOLEAN_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.BYTE_ARRAY;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.DOUBLE;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.DOUBLE_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.FLOAT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.FLOAT_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.INTEGER;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.INTEGER_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.LONG;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.LONG_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.SHORT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.SHORT_OBJECT;
import static com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType.STRING;
import static javax.lang.model.element.Modifier.PUBLIC;

public class GetResolverGenerator implements Generator<StorIOContentResolverTypeMeta> {

    private static final String SUFFIX = "StorIOContentResolverGetResolver";

    @NotNull
    public static String generateName(@NotNull StorIOContentResolverTypeMeta storIOSQLiteTypeMeta) {
        return storIOSQLiteTypeMeta.simpleName + SUFFIX;
    }

    @NotNull
    public JavaFile generateJavaFile(@NotNull final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta) {
        final ClassName storIOContentResolverTypeClassName = ClassName.get(storIOContentResolverTypeMeta.packageName, storIOContentResolverTypeMeta.simpleName);

        final TypeSpec getResolver = TypeSpec.classBuilder(generateName(storIOContentResolverTypeMeta))
                .addJavadoc("Generated resolver for Get Operation\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.contentresolver.operations.get", "DefaultGetResolver"), storIOContentResolverTypeClassName))
                .addMethod(createMapFromCursorMethodSpec(storIOContentResolverTypeMeta, storIOContentResolverTypeClassName))
                .build();

        return JavaFile
                .builder(storIOContentResolverTypeMeta.packageName, getResolver)
                .indent(INDENT)
                .build();
    }

    @NotNull
    private MethodSpec createMapFromCursorMethodSpec(@NotNull StorIOContentResolverTypeMeta storIOContentResolverTypeMeta, @NotNull ClassName storIOContentResolverTypeClassName) {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("mapFromCursor")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(storIOContentResolverTypeClassName)
                .addParameter(ParameterSpec.builder(ClassName.get("android.database", "Cursor"), "cursor")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addStatement("$T object = new $T()", storIOContentResolverTypeClassName, storIOContentResolverTypeClassName)
                .addCode("\n");

        for (final StorIOContentResolverColumnMeta columnMeta : storIOContentResolverTypeMeta.columns.values()) {
            final String columnIndex = "cursor.getColumnIndex(\"" + columnMeta.storIOColumn.name() + "\")";

            final String getFromCursor;

            final JavaType javaType = columnMeta.javaType;

            if (javaType == BOOLEAN || javaType == BOOLEAN_OBJECT) {
                getFromCursor = "getInt(" + columnIndex + ") == 1";
            } else if (javaType == SHORT || javaType == SHORT_OBJECT) {
                getFromCursor = "getShort(" + columnIndex + ")";
            } else if (javaType == INTEGER || javaType == INTEGER_OBJECT) {
                getFromCursor = "getInt(" + columnIndex + ")";
            } else if (javaType == LONG || javaType == LONG_OBJECT) {
                getFromCursor = "getLong(" + columnIndex + ")";
            } else if (javaType == FLOAT || javaType == FLOAT_OBJECT) {
                getFromCursor = "getFloat(" + columnIndex + ")";
            } else if (javaType == DOUBLE || javaType == DOUBLE_OBJECT) {
                getFromCursor = "getDouble(" + columnIndex + ")";
            } else if (javaType == STRING) {
                getFromCursor = "getString(" + columnIndex + ")";
            } else if (javaType == BYTE_ARRAY) {
                getFromCursor = "getBlob(" + columnIndex + ")";
            } else {
                throw new ProcessingException(columnMeta.element, "Can not generate GetResolver for field");
            }

            final boolean isBoxed = javaType.isBoxedType();
            if (isBoxed) { // otherwise -> if primitive and value from cursor null -> fail early
                builder.beginControlFlow("if(!cursor.isNull($L))", columnIndex);
            }

            builder.addStatement("object.$L = cursor.$L", columnMeta.fieldName, getFromCursor);

            if (isBoxed) {
                builder.endControlFlow();
            }
        }

        return builder
                .addCode("\n")
                .addStatement("return object")
                .build();
    }
}
