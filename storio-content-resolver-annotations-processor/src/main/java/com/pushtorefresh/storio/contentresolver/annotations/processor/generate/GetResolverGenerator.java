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
        return storIOSQLiteTypeMeta.getSimpleName() + SUFFIX;
    }

    @NotNull
    public JavaFile generateJavaFile(@NotNull final StorIOContentResolverTypeMeta storIOContentResolverTypeMeta) {
        final ClassName storIOContentResolverTypeClassName = ClassName.get(
            storIOContentResolverTypeMeta.getPackageName(),
            storIOContentResolverTypeMeta.getSimpleName());

        final TypeSpec getResolver = TypeSpec.classBuilder(generateName(storIOContentResolverTypeMeta))
                .addJavadoc("Generated resolver for Get Operation\n")
                .addModifiers(PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.pushtorefresh.storio.contentresolver.operations.get", "DefaultGetResolver"), storIOContentResolverTypeClassName))
                .addMethod(storIOContentResolverTypeMeta.getNeedCreator() ? createMapFromCursorWithCreatorMethodSpec(storIOContentResolverTypeMeta, storIOContentResolverTypeClassName)
                        : createMapFromCursorMethodSpec(storIOContentResolverTypeMeta, storIOContentResolverTypeClassName))
                .build();

        return JavaFile
                .builder(storIOContentResolverTypeMeta.getPackageName(), getResolver)
                .indent(INSTANCE.getINDENT())
                .build();
    }

    @NotNull
    private MethodSpec createMapFromCursorMethodSpec(@NotNull StorIOContentResolverTypeMeta storIOContentResolverTypeMeta, @NotNull ClassName storIOContentResolverTypeClassName) {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("mapFromCursor")
                .addJavadoc("{@inheritDoc}\n")
                .addAnnotation(Override.class)
                .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                .addModifiers(PUBLIC)
                .returns(storIOContentResolverTypeClassName)
                .addParameter(ParameterSpec.builder(ClassName.get("android.database", "Cursor"), "cursor")
                        .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                        .build())
                .addStatement("$T object = new $T()", storIOContentResolverTypeClassName, storIOContentResolverTypeClassName)
                .addCode("\n");

        for (final StorIOContentResolverColumnMeta columnMeta : storIOContentResolverTypeMeta
            .getColumns().values()) {
            final String columnIndex = "cursor.getColumnIndex(\"" + columnMeta.getStorIOColumn().name() + "\")";

            final JavaType javaType = columnMeta.getJavaType();

            final String getFromCursor = INSTANCE
                .getFromCursorString(columnMeta, javaType, columnIndex);

            final boolean isBoxed = javaType.isBoxedType();
            if (isBoxed) { // otherwise -> if primitive and value from cursor null -> fail early
                builder.beginControlFlow("if (!cursor.isNull($L))", columnIndex);
            }

            builder.addStatement("object.$L = cursor.$L", columnMeta.getElementName(), getFromCursor);

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
                .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                .addModifiers(PUBLIC)
                .returns(storIOSQLiteTypeClassName)
                .addParameter(ParameterSpec.builder(ClassName.get("android.database", "Cursor"), "cursor")
                        .addAnnotation(INSTANCE.getANDROID_NON_NULL_ANNOTATION_CLASS_NAME())
                        .build())
                .addCode("\n");

        final StringBuilder paramsBuilder = new StringBuilder();
        paramsBuilder.append("(");
        boolean first = true;
        for (final StorIOContentResolverColumnMeta columnMeta : storIOContentResolverTypeMeta.getOrderedColumns()) {
            final String columnIndex = "cursor.getColumnIndex(\"" + columnMeta.getStorIOColumn().name() + "\")";

            final JavaType javaType = columnMeta.getJavaType();

            final String getFromCursor = INSTANCE
                .getFromCursorString(columnMeta, javaType, columnIndex);

            final TypeName name = TypeName.get(((ExecutableElement) columnMeta.getElement()).getReturnType());

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

        if (storIOContentResolverTypeMeta.getCreator().getKind() == ElementKind.CONSTRUCTOR) {
            builder.addStatement("$T object = new $T" + paramsBuilder.toString(), storIOSQLiteTypeClassName, storIOSQLiteTypeClassName);
        } else {
            builder.addStatement("$T object = $T.$L", storIOSQLiteTypeClassName, storIOSQLiteTypeClassName,
                    storIOContentResolverTypeMeta.getCreator().getSimpleName() + paramsBuilder.toString());
        }

        return builder
                .addCode("\n")
                .addStatement("return object")
                .build();
    }
}
