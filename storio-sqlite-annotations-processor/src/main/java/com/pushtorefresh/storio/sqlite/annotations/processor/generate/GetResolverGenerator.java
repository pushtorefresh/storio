package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.common.annotations.processor.generate.Generator;
import com.pushtorefresh.storio.common.annotations.processor.introspection.JavaType;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;
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

public class GetResolverGenerator implements Generator<StorIOSQLiteTypeMeta> {

    private static final String SUFFIX = "StorIOSQLiteGetResolver";

    @NotNull
    public static String generateName(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta) {
        return storIOSQLiteTypeMeta.simpleName + SUFFIX;
    }

    @NotNull
    public JavaFile generateJavaFile(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta) {
        final ClassName storIOSQLiteTypeClassName = ClassName.get(storIOSQLiteTypeMeta.packageName, storIOSQLiteTypeMeta.simpleName);

        final TypeSpec getResolver = TypeSpec.classBuilder(generateName(storIOSQLiteTypeMeta))
                .addJavadoc("Generated resolver for Get Operation.\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.sqlite.operations.get", "DefaultGetResolver"), storIOSQLiteTypeClassName))
                .addMethod(storIOSQLiteTypeMeta.needCreator ? createMapFromCursorWithCreatorMethodSpec(storIOSQLiteTypeMeta, storIOSQLiteTypeClassName)
                        : createMapFromCursorMethodSpec(storIOSQLiteTypeMeta, storIOSQLiteTypeClassName))
                .build();

        return JavaFile
                .builder(storIOSQLiteTypeMeta.packageName, getResolver)
                .indent(INDENT)
                .build();
    }

    @NotNull
    private MethodSpec createMapFromCursorMethodSpec(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta, @NotNull ClassName storIOSQLiteTypeClassName) {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("mapFromCursor")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                .addModifiers(PUBLIC)
                .returns(storIOSQLiteTypeClassName)
                .addParameter(ParameterSpec.builder(ClassName.get("android.database", "Cursor"), "cursor")
                        .addAnnotation(ANDROID_NON_NULL_ANNOTATION_CLASS_NAME)
                        .build())
                .addStatement("$T object = new $T()", storIOSQLiteTypeClassName, storIOSQLiteTypeClassName)
                .addCode("\n");

        for (final StorIOSQLiteColumnMeta columnMeta : storIOSQLiteTypeMeta.columns.values()) {
            final String columnIndex = "cursor.getColumnIndex(\"" + columnMeta.storIOColumn.name() + "\")";

            final JavaType javaType = columnMeta.javaType;

            final String getFromCursor = getFromCursorString(columnMeta, javaType, columnIndex);

            final boolean isBoxed = javaType.isBoxedType();
            if (isBoxed) { // otherwise -> if primitive and value from cursor null -> fail early
                builder.beginControlFlow("if (!cursor.isNull($L))", columnIndex);
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
    private MethodSpec createMapFromCursorWithCreatorMethodSpec(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta, @NotNull ClassName storIOSQLiteTypeClassName) {
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

        final StringBuilder paramsBuilder = new StringBuilder();
        paramsBuilder.append("(");
        boolean first = true;
        for (final StorIOSQLiteColumnMeta columnMeta : storIOSQLiteTypeMeta.getOrderedColumns()) {
            final String columnIndex = "cursor.getColumnIndex(\"" + columnMeta.storIOColumn.name() + "\")";

            final JavaType javaType = columnMeta.javaType;

            final String getFromCursor = getFromCursorString(columnMeta, javaType, columnIndex);

            final TypeName name = TypeName.get(((ExecutableElement) columnMeta.element).getReturnType());

            final boolean isBoxed = javaType.isBoxedType();
            if (isBoxed) { // otherwise -> if primitive and value from cursor null -> fail early
                builder.addStatement("$T $L = null", name, columnMeta.getRealElementName());
                builder.beginControlFlow("if (!cursor.isNull($L))", columnIndex);
                builder.addStatement("$L = cursor.$L", columnMeta.getRealElementName(), getFromCursor);
                builder.endControlFlow();
            } else {
                builder.addStatement("$T $L = cursor.$L", name, columnMeta.getRealElementName(), getFromCursor);
            }

            if (!first) {
                paramsBuilder.append(", ");
            }
            first = false;
            paramsBuilder.append(columnMeta.getRealElementName());
        }
        paramsBuilder.append(")");
        builder.addCode("\n");

        if (storIOSQLiteTypeMeta.creator.getKind() == ElementKind.CONSTRUCTOR) {
            builder.addStatement("$T object = new $T" + paramsBuilder.toString(), storIOSQLiteTypeClassName, storIOSQLiteTypeClassName);
        } else {
            builder.addStatement("$T object = $T.$L", storIOSQLiteTypeClassName, storIOSQLiteTypeClassName,
                    storIOSQLiteTypeMeta.creator.getSimpleName() + paramsBuilder.toString());
        }

        return builder
                .addCode("\n")
                .addStatement("return object")
                .build();
    }
}
