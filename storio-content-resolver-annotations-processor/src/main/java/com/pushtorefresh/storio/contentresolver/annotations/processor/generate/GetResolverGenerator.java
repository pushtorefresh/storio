package com.pushtorefresh.storio.contentresolver.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverColumnMeta;
import com.pushtorefresh.storio.contentresolver.annotations.processor.introspection.StorIOContentResolverTypeMeta;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

import static com.pushtorefresh.storio.common.annotations.processor.generate.Common.ANDROID_NON_NULL_ANNOTATION_CLASS_NAME;
import static com.pushtorefresh.storio.common.annotations.processor.generate.Common.INDENT;
import static com.pushtorefresh.storio.common.annotations.processor.generate.Common.getFromCursorString;
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
                .addMethod(storIOContentResolverTypeMeta.needCreator ? createMapFromCursorWithCreatorMethodSpec(storIOContentResolverTypeMeta, storIOContentResolverTypeClassName)
                        : createMapFromCursorMethodSpec(storIOContentResolverTypeMeta, storIOContentResolverTypeClassName))
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

            final JavaType javaType = columnMeta.javaType;

            final String getFromCursor = getFromCursorString(columnMeta, javaType, columnIndex);

            final boolean isBoxed = javaType.isBoxedType();
            if (isBoxed) { // otherwise -> if primitive and value from cursor null -> fail early
                builder.beginControlFlow("if(!cursor.isNull($L))", columnIndex);
            }

            builder.addStatement("object.$L = cursor.$L", columnMeta.elementName, getFromCursor);

            if (isBoxed) {
                builder.endControlFlow();
            }
        }

        return builder
                .addCode("\n")
                .addStatement("return object")
                .build();
    }

    @NotNull
    private MethodSpec createMapFromCursorWithCreatorMethodSpec(@NotNull StorIOContentResolverTypeMeta storIOContentResolverTypeMeta, @NotNull ClassName storIOSQLiteTypeClassName) {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("mapFromCursor")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(storIOSQLiteTypeClassName)
                .addParameter(ParameterSpec.builder(ClassName.get("android.database", "Cursor"), "cursor")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addCode("\n");

        // We don't know the name of the variable, just the type. So just use generic parameter names
        int paramCount = 1;
        final StringBuilder paramsBuilder = new StringBuilder();
        paramsBuilder.append("(");
        boolean first = true;
        for (final StorIOContentResolverColumnMeta columnMeta : storIOContentResolverTypeMeta.columns.values()) {
            final String columnIndex = "cursor.getColumnIndex(\"" + columnMeta.storIOColumn.name() + "\")";

            final JavaType javaType = columnMeta.javaType;

            final String getFromCursor = getFromCursorString(columnMeta, javaType, columnIndex);

            final TypeName name = TypeName.get(((ExecutableElement) columnMeta.element).getReturnType());

            final boolean isBoxed = javaType.isBoxedType();
            if (isBoxed) { // otherwise -> if primitive and value from cursor null -> fail early
                builder.addStatement("$T param" + paramCount + " = null", name);
                builder.beginControlFlow("if(!cursor.isNull($L))", columnIndex);
                builder.addStatement("param" + paramCount + " = cursor.$L", getFromCursor);
                builder.endControlFlow();
            } else {
                builder.addStatement("$T param" + paramCount + " = cursor.$L", name, getFromCursor);
            }

            if (!first) {
                paramsBuilder.append(", ");
            }
            first = false;
            paramsBuilder.append("param" + paramCount);

            paramCount++;
        }
        paramsBuilder.append(")");
        builder.addCode("\n");

        if (storIOContentResolverTypeMeta.creator.getKind() == ElementKind.CONSTRUCTOR) {
            builder.addStatement("$T object = new $T" + paramsBuilder.toString(), storIOSQLiteTypeClassName, storIOSQLiteTypeClassName);
        } else {
            builder.addStatement("$T object = $T." + storIOContentResolverTypeMeta.creator.getSimpleName() + paramsBuilder.toString(), storIOSQLiteTypeClassName, storIOSQLiteTypeClassName);
        }

        return builder
                .addCode("\n")
                .addStatement("return object")
                .build();
    }
}
